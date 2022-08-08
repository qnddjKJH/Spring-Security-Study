package com.study.mysecurity.domain.user;

import lombok.Getter;
import org.springframework.security.core.userdetails.User;

@Getter
public class UserDetailsAdapter extends User {

    private com.study.mysecurity.domain.user.User user;

    public UserDetailsAdapter(com.study.mysecurity.domain.user.User user) {
        super(user.getUsername(), user.getPassword(), user.getAuthorities());
        this.user = user;
    }

}
