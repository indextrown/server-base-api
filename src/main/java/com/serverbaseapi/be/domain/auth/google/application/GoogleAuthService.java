package com.serverbaseapi.be.domain.auth.google.application;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.serverbaseapi.be.common.util.Logger;
import com.serverbaseapi.be.domain.auth.google.config.GoogleProperties;
import com.serverbaseapi.be.domain.auth.google.dto.response.GoogleUserInfoResponseDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service // 스프링잌 자동으로 bean으로 등록해줘서 다른 곳에서 @Autowired 또는 생성자 주입으로 사용가능
@RequiredArgsConstructor
public class GoogleAuthService {
    private final GoogleProperties googleProperties;

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

    // ----- Google Mobile Register -----

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

}
