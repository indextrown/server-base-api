package com.serverbaseapi.be.domain.auth.dto.request;

import com.serverbaseapi.be.common.enums.Role;
import lombok.Getter;

@Getter
public class SignupRequestDto {
    private String uid;
    private String email;
    private String nickname;
    private Role role;

}
