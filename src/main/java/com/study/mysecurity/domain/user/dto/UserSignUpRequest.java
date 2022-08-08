package com.study.mysecurity.domain.user.dto;

import com.study.mysecurity.domain.user.Role;
import com.study.mysecurity.domain.user.User;
import com.sun.istack.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserSignUpRequest {
    @NotNull
    private String email;

    @NotNull
    private String password;

    private String name;

    public User toEntity() {
        return User.builder()
                .email(email)
                .password(password)
                .name(name)
                .role(Role.USER)
                .build();
    }
}
