package com.serverbaseapi.be.domain.auth.dto.response;

import com.serverbaseapi.be.common.enums.Role;
import com.serverbaseapi.be.domain.users.entity.Provider;
import com.serverbaseapi.be.domain.users.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *@Builder
 * - Lombok이 빌더 패턴을 자동으로 생성
 * - builder() → 필드 설정 → build() 순서로 객체 생성 가능
 * - 가독성이 좋고, 선택적 필드가 많은 DTO에서 특히 유용
 *
 * from(User user)
 * - User 엔티티를 DTO로 변환하는 정적 팩토리 메서드
 * - 각 builder 메서드는 DTO의 해당 필드에 값을 세팅함:
 *     .uid(user.getUid())         → 사용자 UID 복사
 *     .userUuid(user.getUuid())   → 사용자 고유 UUID 복사
 *     .provider(user.getProvider()) → Google/Apple/Kakao 등 Provider 설정
 *     .email(user.getEmail())     → 이메일 설정
 *     .nickname(user.getNickname()) → 닉네임 설정
 *     .role(user.getRole())       → 사용자 권한 설정
 *     .build()                    → DTO 객체 완성
 */
@Getter
@NoArgsConstructor // 파라미터가 하나도 없는 기본 생성자(= 인자 없는 생성자)를 자동으로 만들어주는 Lombok 어노테이션
public class SignupResponseDto {
    private String uid;
    private String userUuid;
    private Provider provider;
    private String email;
    private String nickname;
    private Role role;

    @Builder
    public SignupResponseDto(String uid,
                             String userUuid,
                             Provider provider,
                             String email,
                             String nickname,
                             Role role) {
        this.uid = uid;
        this.userUuid = userUuid;
        this.provider = provider;
        this.email = email;
        this.nickname = nickname;
        this.role = role;
    }

    public static SignupResponseDto from(User user) {
        return SignupResponseDto.builder()
                .uid(user.getUid())
                .userUuid(user.getUuid())
                .provider(user.getProvider())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getRole())
                .build();
    }
}
