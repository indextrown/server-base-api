package com.serverbaseapi.be.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// created_at / updated_at 자동 기록 기능을 켜주는 설정 파일
@Configuration // 이 클래스가 Spring 설정 클래스라는 의미. 스프링이 애플리케이션 시작할 때 이 클래스를 읽고 필요한 설정을 적용함.
@EnableJpaAuditing  // JPA Auditing을 활성화하는 어노테이션
public class JpaConfig {
}
