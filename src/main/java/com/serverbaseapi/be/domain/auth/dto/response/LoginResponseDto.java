package com.serverbaseapi.be.domain.auth.dto.response;

import com.serverbaseapi.be.common.enums.Role;
import com.serverbaseapi.be.domain.users.entity.Provider;
import com.serverbaseapi.be.domain.users.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginResponseDto {
    private String uid;
    private String userUuid;
    private Provider provider;
    private String email;
    private String nickname;
    private Role role;

    /**
     * @Builder = 객체 생성 시 값을 하나씩 명확하게 넣을 수 있게 해주는 Lombok 기능
     *
     * - new LoginResponseDto(uid, userUuid, provider, email, nickname, role)
     *   ➜ 순서 외우기 힘듦, 실수 발생 가능
     *
     * - LoginResponseDto.builder()
     *       .uid(uid)
     *       .userUuid(userUuid)
     *       .provider(provider)
     *       .email(email)
     *       .nickname(nickname)
     *       .role(role)
     *       .build();
     *
     *   ➜ 어떤 필드에 어떤 값을 넣는지 명확하게 보이고,
     *     순서 실수 방지, 선택적으로 값 넣기 가능
     *
     * Builder는 아래 생성자를 기반으로 Lombok이 자동 생성함
     */
    @Builder
    public LoginResponseDto(String uid,
                            String userUuid,
                            Provider provider,
                            String email,
                            String nickname,
                            Role role
    ) {
        this.uid = uid;
        this.userUuid = userUuid;
        this.provider = provider;
        this.email = email;
        this.nickname = nickname;
        this.role = role;
    }

    /**
     * 정적 팩토리 메서드 from(User user)
     *
     * 엔티티(User)를 전달하면 LoginResponseDto로 변환해주는 메서드.
     * 서비스단에서 자주 사용함.
     *
     * 예)
     * LoginResponseDto dto = LoginResponseDto.from(user);
     *
     * 이 메서드 내부에서도 Builder를 사용해 DTO를 생성한다.
     */
    public static LoginResponseDto from(User user) {
        return LoginResponseDto.builder()
                .uid(user.getUid())
                .userUuid(user.getUuid())
                .provider(user.getProvider())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getRole())
                .build();
    }
}
