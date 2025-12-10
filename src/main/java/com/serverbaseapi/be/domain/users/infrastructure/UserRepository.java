package com.serverbaseapi.be.domain.users.infrastructure;

import com.serverbaseapi.be.domain.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
