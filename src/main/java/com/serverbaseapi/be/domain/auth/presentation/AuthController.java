package com.serverbaseapi.be.domain.auth.presentation;

import com.serverbaseapi.be.domain.auth.application.AuthService;
import com.serverbaseapi.be.domain.auth.dto.request.AutoLoginRequestDto;
import com.serverbaseapi.be.domain.auth.dto.request.SignupRequestDto;
import com.serverbaseapi.be.domain.auth.dto.response.LoginResponseDto;
import com.serverbaseapi.be.domain.auth.dto.response.SignupResponseDto;
import com.serverbaseapi.be.domain.auth.google.application.GoogleAuthService;
import com.serverbaseapi.be.domain.auth.google.dto.request.GoogleAppLoginRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final GoogleAuthService googleAuthService;
    private final AuthService authService;

    /**
     * [@RequiredArgsConstructor]
    public AuthController(GoogleAuthService googleAuthService, AuthService authService) {
        this.googleAuthService = googleAuthService;
        this.authService = authService;
    }
     */

    // ----- 앱 자동 로그인 -----
    @Operation(summary = "자동 로그인,",
            description = "앱 로컬에 저장된 uuid로 자동 로그인합니다.",
            tags = {"[Auth] 자동 로그인"}
    )
    @PostMapping("/autoLogin")
    public ResponseEntity<LoginResponseDto> autoLogin(@RequestBody AutoLoginRequestDto autoLoginRequestDto) {
        LoginResponseDto loginResponseDto = authService.autoLogin(autoLoginRequestDto);
        return ResponseEntity.ok(loginResponseDto);
    }

    // ----- 구글 로그인 -----
    @PostMapping("/google/mobile/login")
    @Operation(summary = "[App] 구글 로그인",
            description = "구글 로그인 버튼을 눌러 발급받은 id_token을 사용해 로그인합니다.(서버 로그인 아닙니다.)",
            tags = {"[Auth] 구글"},
            operationId = "1_googleLogin"
    )
    public ResponseEntity<LoginResponseDto> googleMobileLogin(@RequestBody GoogleAppLoginRequestDto googleAppLoginRequestDto) {
        LoginResponseDto loginResponseDto = googleAuthService.mobileLogin(googleAppLoginRequestDto);
        return ResponseEntity.ok(loginResponseDto);
    }

    // ----- 구글 회원가입 -----
    @Operation(summary = "[App] 구글 + 데이터베이스 회원가입"
            ,description = "구글 앱에서 발급받은 id_token을 사용해 로그인합니다.",
            tags = {"[Auth] 구글"},
            operationId = "2_googleLogin"
    )
    @PostMapping("/google/signup")
    public ResponseEntity<SignupResponseDto> googleSignup(@RequestBody SignupRequestDto signupRequestDto) {
        SignupResponseDto signupResponseDto = googleAuthService.signup(signupRequestDto);
        return ResponseEntity.ok(signupResponseDto);
    }
}
