package com.serverbaseapi.be.domain.auth.google.application;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.serverbaseapi.be.common.enums.Role;
import com.serverbaseapi.be.common.util.Logger;
import com.serverbaseapi.be.domain.auth.dto.request.SignupRequestDto;
import com.serverbaseapi.be.domain.auth.dto.response.LoginResponseDto;
import com.serverbaseapi.be.domain.auth.dto.response.SignupResponseDto;
import com.serverbaseapi.be.domain.auth.google.config.GoogleProperties;
import com.serverbaseapi.be.domain.auth.google.dto.request.GoogleAppLoginRequestDto;
import com.serverbaseapi.be.domain.auth.google.dto.response.GoogleTokenResponseDto;
import com.serverbaseapi.be.domain.auth.google.dto.response.GoogleUserInfoResponseDto;
import com.serverbaseapi.be.domain.users.entity.Provider;
import com.serverbaseapi.be.domain.users.entity.User;
import com.serverbaseapi.be.domain.users.infrastructure.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Service // 스프링잌 자동으로 bean으로 등록해줘서 다른 곳에서 @Autowired 또는 생성자 주입으로 사용가능
@RequiredArgsConstructor
public class GoogleAuthService {
    private final GoogleProperties googleProperties;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    // 스프링이 이 객체를 다 만들고, 필요한 값들까지 다 넣어준 후 자동으로 실행되는 메서드.(객체가 완벽하게 준비된 후 test()가 실행됨.)
    @PostConstruct
    public void init() {
        test();
    }

    public void test() {
        String clientId = googleProperties.getClientId();
        String clientSecret = googleProperties.getClientSecret();
        Logger.d(clientId);
        Logger.d(clientSecret);
    }

    // ----- Google Mobile Login -----
    // 로그인 & 회원가입(idToken, email만)을 동시에 처리하는 API
    public LoginResponseDto mobileLogin(GoogleAppLoginRequestDto googleAppLoginRequestDto) {
        GoogleUserInfoResponseDto googleUserInfoResponseDto = parseIdTokenToProfile(googleAppLoginRequestDto.getIdToken());
        String uid = googleUserInfoResponseDto.getSub();
        String email = googleUserInfoResponseDto.getEmail();
        Logger.d(uid);
        Logger.d(email);

        User user = upsertyByuid(uid, email);
        Logger.d(user.getUuid());
        System.out.println("구글 로그인" + LoginResponseDto.from(user));
        return LoginResponseDto.from(user);
    }

    // ----- Google Mobile Register -----
    // 모바일 최종 회원가입 완료 API
    @Transactional
    public SignupResponseDto signup(SignupRequestDto signupRequestDto) {
        // 1) 이미 존재하는 닉네임인지 체크
        if (userRepository.existsByNickname(signupRequestDto.getNickname())) {
            throw new IllegalStateException("이미 사용 중인 닉네임입니다. ");
        }

        // 2) 이미 존재하는 유저인지 체크
        User user = userRepository.findByUid(signupRequestDto.getUid())
                .orElseThrow(() -> new IllegalStateException("유저를 찾을 수 없습니다. "));

        // 3) 엔티티(User)에게 “회원가입 완료 정보” 적용
        user.completeSignup(signupRequestDto);

        // 4) 변경된 유저 정보를 DB에 반영
        userRepository.save(user);

        System.out.println("팝팡 회원가입" + SignupResponseDto.from(user));

        // 5) 응답 DTO로 변환하여 반환
        return SignupResponseDto.from(user);
    }


    // ----- Google Private Logic -----

    /**
     * [iOS / Android]
     * Google SDK → ID Token 획득
     *          ↓
     * 서버에 ID Token 전달
     *          ↓
     * 서버에서 ID Token 검증(parseIdTokenToProfile)
     *          ↓
     * UID(sub) 사용해 Upsert
     *          ↓
     * LoginResponseDto 반환
     *
     * [Web]
     * [1] Authorization Code 받음
     *          ↓
     *  getAccessToken(code)
     *          ↓
     *   (access_token + id_token 받음)
     *          ↓
     *  parseIdTokenToProfile(id_token)
     *          ↓
     *     GoogleUserInfoResponseDto(sub, email…)
     *          ↓
     *  upsertByUid(sub, email)
     *          ↓
     *  LoginResponseDto.from(user)
     *          ↓
     *    클라이언트에게 로그인 성공 응답
     */

    /**
     * Google ID Token(id_token)을 검증하고
     * 토큰 안에 들어있는 사용자 정보를 추출하여 DTO로 변환하는 메서드.
     * - 구글이 발급한 토큰이 맞는지(서명 검증)
     * - 만료되지 않았는지
     * - 우리 앱(clientId, iOS clientId)을 위한 토큰인지(audience 검증)
     *   등을 모두 체크한 후 사용자 정보(sub, email, name, picture)를 꺼낸다.
     */
    private GoogleUserInfoResponseDto parseIdTokenToProfile(String idToken) {
        try {
            // 1) Google ID Token 검증기(verifier) 생성
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),            // HTTP 통신용 기본 Transport
                    GsonFactory.getDefaultInstance()   // JacksonFactory → GsonFactory
                    // new JacksonFactory()            // JSON 처리용 Factory(deprecated)
            )
                    // idToken이 우리 앱(clientId, iOS clientId)용인지 확인
                    .setAudience(List.of(
                            googleProperties.getClientId(),     // Android/Web clientId
                            googleProperties.getIosClientId()   // iOS clientId
                    ))
                    .build();                                   // Builder 패턴으로 verifier 객체 완성

            // 2) idToken 검증 (서명, 만료, audience 등)
            GoogleIdToken token = verifier.verify(idToken);
            if (token == null) {
                throw new IllegalStateException("Invalid Google id_token");
            }

            // 3) payload(토큰 내부 사용자 정보) 추출
            GoogleIdToken.Payload p = token.getPayload();

            // 4) payload 안에서 필요한 사용자 정보 가져오기
            String sub = p.getSubject();                  // Google UID
            String email = p.getEmail();                  // 사용자 이메일
            String name = (String) p.get("name");         // 프로필 이름
            String picture = (String) p.get("picture");   // 프로필 사진 URL

            // 5) DTO로 변환하여 서비스 계층에서 사용하기 편하게 반환
            return new GoogleUserInfoResponseDto(sub, email, name, picture);

        } catch (Exception e) {
            // id_token 검증 실패 시 예외 처리
            throw new IllegalStateException("Google id_token verification failed", e);
        }
    }

    /**
     * [웹전용 / 모바일은 라비르러리로 요청하는 것 같음 getAccessToken]
     * Authorization Code(인가코드)를 Google Token 서버로 전달하여
     * Access Token + ID Token을 받아오는 함수.
     *
     * 역할 요약:
     * 1) 토큰 요청에 필요한 파라미터 구성
     * 2) Google Token API에 POST 요청
     * 3) 응답(body)에서 access_token / id_token 꺼냄
     * 4) GoogleTokenResponseDto 로 반환
     */
    private GoogleTokenResponseDto getAccessToken(String authorizationCode) {
        try {

            // 1) Google Token 서버에 보낼 Form 파라미터 구성
            MultiValueMap<String , String> form = new LinkedMultiValueMap<>();
            form.add("grant_type", "authorization_code");
            form.add("code", authorizationCode);
            form.add("client_id", googleProperties.getClientId());
            form.add("client_secret", googleProperties.getClientSecret());
            form.add("redirect_uri", googleProperties.getRedirectUri());

            // 2) 요청 헤더 설정 (Google OAuth는 반드시 x-www-form-urlencoded 사용)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // 3) Google Token 엔드포인트로 POST 요청 전송
            ResponseEntity<GoogleTokenResponseDto> res = restTemplate.exchange(
                    googleProperties.getTokenUri(),     // https://oauth2.googleapis.com/token
                    HttpMethod.POST,
                    new HttpEntity<>(form, headers),    // body(form) + headers
                    GoogleTokenResponseDto.class        // 응답 DTO 타입
            );

            // 4) 응답에서 body와 access_token 검증
            GoogleTokenResponseDto body = res.getBody();
            if (body == null || body.getAccessToken() == null) {
                throw new IllegalStateException("Google token exchange failed (empty response)");
            }

            // Access Token / ID Token / expires_in 등을 포함한 DTO 반환
            return body;

        } catch (Exception e) {
            throw new IllegalStateException("Failed to exchange Google token", e);
        }
    }

    // update + insert(존재하면 값을 반환, 존재하지 않으면 insert 후 값을 반환
    // UID로 기존 유저를 찾고 → 없으면 새로 만들고 → 있으면 그대로 반환하는 함수
    private User upsertyByuid(String uid, String email) {
        return userRepository.findByUid(uid)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .uid(uid)
                                .provider(Provider.GOOGLE)
                                .role(Role.MEMBER)
                                .email(email)
                                .build()
                ));
    }
}
