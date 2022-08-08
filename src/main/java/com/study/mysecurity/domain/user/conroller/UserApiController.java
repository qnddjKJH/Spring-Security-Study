package com.study.mysecurity.domain.user.conroller;

import com.study.mysecurity.domain.user.UserDetailsAdapter;
import com.study.mysecurity.domain.user.dto.UserSignUpRequest;
import com.study.mysecurity.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@RequestMapping("/api/user")
@RestController
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    @GetMapping
    public String test(@AuthenticationPrincipal UserDetailsAdapter user) {
        log.info("security test :: user :: {}", user.getUser());
        return user.getUser().toString();
    }

    @PostMapping("/login")
    public String login(@RequestParam String email) {
        log.info("request String :: {}", email);
        String username = userService.loadUserByUsername(email).getUsername();

        WebClient webClient = WebClient.create("http://localhost:8080");
        webClient.get()
                .uri("/api/user")
                .retrieve();

        return username;
    }

    @PostMapping("/signUp")
    public String signUp(@RequestBody UserSignUpRequest request) throws Exception {
        return userService.signUp(request).toString();
    }

}
