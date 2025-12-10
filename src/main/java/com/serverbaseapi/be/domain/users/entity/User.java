package com.serverbaseapi.be.domain.users.entity;

import com.serverbaseapi.be.common.entity.BaseEntity;
import com.serverbaseapi.be.common.enums.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "suid", nullable = true, unique = true, length = 255)
    private String suid;

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
}
