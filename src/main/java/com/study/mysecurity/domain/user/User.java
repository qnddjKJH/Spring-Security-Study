package com.study.mysecurity.domain.user;

import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;

@Entity
@Table(name = "users")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String name;

    /**
     * 비즈니스 로직
     * 비밀번호 암호화
     */
    public void hashPassword(BCryptPasswordEncoder passwordEncoder) {
        password = passwordEncoder.encode(password);
    }
}
