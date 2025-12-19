package com.serverbaseapi.be.domain.users.infrastructure;

import com.serverbaseapi.be.domain.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Spring Bean으로 등록
// 명시적으로 적지 않아도 동작하지만, "이 인터페이스는 Repository 역할"이라는 의도를 표현
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // User 엔티티를 대상으로
    // 기본 CRUD(save, findById, findAll, delete 등)를 제공받음
    // Long → User 엔티티의 PK 타입

    // uid로 유저를 조회
    // 결과가 없을 수 있으므로 Optional로 감쌈
    // ex) 소셜 로그인 uid 기반 조회
    Optional<User> findByUid(String uid);

    // 닉네임 중복 여부 확인
    // true  → 이미 존재하는 닉네임
    // false → 사용 가능한 닉네임
    // 회원가입 / 닉네임 설정 시 중복 검사 용도
    Boolean existsByNickname(String nickname);

    // uuid가 일치하고, 삭제되지 않은(deleted = false) 유저만 조회
    // 소프트 삭제(Soft Delete) 패턴 적용
    // 실제로 row를 삭제하지 않고, deleted 플래그로 활성/비활성 관리
    //
    // SQL 개념:
    // SELECT * FROM users WHERE uuid = ? AND deleted = false;
    // Optional<User> findByUuidAndDeletedFalse(String uuid);
    Optional<User> findByUuid(String uuid);
}
