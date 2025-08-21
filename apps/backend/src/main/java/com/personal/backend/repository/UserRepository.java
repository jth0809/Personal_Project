package com.personal.backend.repository;

import com.personal.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일을 기준으로 사용자를 찾는 기능을 정의합니다.
    // 로그인 및 회원가입 시 중복 확인에 필수적으로 사용됩니다.
    // Optional<T>는 결과가 null일 수도 있다는 것을 명확히 하여, NullPointerException을 방지합니다.
    Optional<User> findByEmail(String email);
    List<User> findByEmailIn(Set<String> emails);
}
