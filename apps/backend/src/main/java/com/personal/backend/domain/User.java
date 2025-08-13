package com.personal.backend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users") // 테이블 이름은 'users'로 지정
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_generator")
    @SequenceGenerator(
            name = "user_seq_generator",
            sequenceName = "USER_SEQ",
            allocationSize = 1
    )
    private Long id;

    @Column(nullable = false, unique = true) // 이메일은 고유해야 함
    private String email;

    @Column(nullable = false)
    private String password; // 실제로는 암호화되어 저장됩니다.

    @Column(nullable = false)
    private String username;
    
    @Enumerated(EnumType.STRING) // Enum의 이름을 DB에 문자열로 저장
    @Column(nullable = false)
    private UserRole role;

    @Builder
    public User(String email, String password, String username, UserRole role) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.role = role;
    }
}