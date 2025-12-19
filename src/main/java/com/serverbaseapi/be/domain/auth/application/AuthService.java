package com.serverbaseapi.be.domain.auth.application;

import com.serverbaseapi.be.domain.auth.dto.request.AutoLoginRequestDto;
import com.serverbaseapi.be.domain.auth.dto.response.LoginResponseDto;
import com.serverbaseapi.be.domain.auth.dto.response.SignupResponseDto;
import com.serverbaseapi.be.domain.users.entity.User;
import com.serverbaseapi.be.domain.users.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    public LoginResponseDto autoLogin(AutoLoginRequestDto autoLoginRequestDto) {
        User user = userRepository.findByUuid(autoLoginRequestDto.getUserUuid())
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. "));

        System.out.println("자동 로그인" + LoginResponseDto.from(user));
        return LoginResponseDto.from(user);
    }
}
