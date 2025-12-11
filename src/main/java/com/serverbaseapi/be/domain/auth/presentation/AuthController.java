package com.serverbaseapi.be.domain.auth.presentation;

import com.serverbaseapi.be.domain.auth.dto.request.SignupRequestDto;
import com.serverbaseapi.be.domain.auth.dto.response.LoginResponseDto;
import com.serverbaseapi.be.domain.auth.dto.response.SignupResponseDto;
import com.serverbaseapi.be.domain.auth.google.application.GoogleAuthService;
import com.serverbaseapi.be.domain.auth.google.dto.request.GoogleAppLoginRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final GoogleAuthService googleAuthService;

    public AuthController(GoogleAuthService googleAuthService) {
        this.googleAuthService = googleAuthService;
    }

    // ----- 테스트 -----
    @Operation(summary = "[Test] 테스트"
            ,description = "테스트 코드 입니다.",
            tags = {"[Test] 테스트"}
    )
    @GetMapping("/test")
    public Map<String, String> test(){
        return Map.of("message", "test");
    }

    // ----- 구글 로그인 -----
    @PostMapping("/google/mobile/login")
    @Operation(summary = "[App] 구글 로그인"
            ,description = "구글 로그인 버튼을 눌러 발급받은 id_token을 사용해 로그인합니다.(서버 로그인 아닙니다.)",
            tags = {"[Auth] 구글"}
    )
    public ResponseEntity<LoginResponseDto> googleMobileLogin(@RequestBody GoogleAppLoginRequestDto googleAppLoginRequestDto) {
        LoginResponseDto loginResponseDto = googleAuthService.mobileLogin(googleAppLoginRequestDto);
        return ResponseEntity.ok(loginResponseDto);
    }

    // ----- 구글 회원가입 -----
    @Operation(summary = "[App] 구글 + 데이터베이스 회원가입"
            ,description = "구글 앱에서 발급받은 id_token을 사용해 로그인합니다.",
            tags = {"[Auth] 구글"}
    )
    @PostMapping("/google/signup")
    public ResponseEntity<SignupResponseDto> googleSignup(@RequestBody SignupRequestDto signupRequestDto) {
        SignupResponseDto signupResponseDto = googleAuthService.signup(signupRequestDto);
        return ResponseEntity.ok(signupResponseDto);
    }
}
