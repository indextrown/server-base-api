package com.serverbaseapi.be.domain.users.entity;

import com.serverbaseapi.be.common.entity.BaseEntity;
import com.serverbaseapi.be.common.enums.Role;
import com.serverbaseapi.be.domain.auth.dto.request.SignupRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "uid", nullable = true, unique = true, length = 255)
    private String uid;

    @Column(name = "uuid", nullable = false, length = 36)
    private String uuid;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = true, length = 20)
    private Provider provider;

    @Column(name = "email", nullable = true, length = 255)
    private String email;

    @Column(name = "nickname", nullable = true, length = 255, unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = true, length = 20)
    private Role role;

    @Builder
    public User(Long id,
                String uid,
                String email,
                String nickname,
                Provider provider,
                String uuid,
                Role role
    ) {
        this.id = id;
        this.uid = uid;
        this.email = email;
        this.nickname = nickname;
        this.provider = provider;
        this.uuid = uuid;
        this.role = role;
    }

    public void completeSignup(SignupRequestDto signupRequestDto) {
        this.email = signupRequestDto.getEmail();
        this.nickname = signupRequestDto.getNickname();
    }

    // DB에 INSERT 되기 직전 UUID가 비었으면 자동으로 UUID를 넣어주는 기능
    @PrePersist
    private void ensureUuid() {
        if (this.uuid == null || this.uuid.isBlank()) {
            this.uuid = UUID.randomUUID().toString();
        }
    }
}