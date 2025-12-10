package com.serverbaseapi.be.domain.auth.presentation;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    // ----- 구글 로그인 -----
    @Operation(summary = "[App] 구글 로그인"
            ,description = "구글 앱에서 발급받은 id_token을 사요해 로그인합니다.",
            tags = {"[Auth] 구글"}
    )
    @GetMapping("/test")
    public Map<String, String> test(){
        return Map.of("message", "test");
    }
}
