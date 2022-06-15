package com.study.mysecurity.domain.user.conroller;

import com.study.mysecurity.domain.user.User;
import com.study.mysecurity.domain.user.dto.UserSignUpRequest;
import com.study.mysecurity.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/")
    public String userList() {
        return "user/userList";
    }

    @GetMapping("/{email}")
    public String profile(@PathVariable String email, Model model) {
        userService.findUser(email);

        return "user/profile";
    }

    @GetMapping("/signIn")
    public String signIn() {
        return "user/signIn";
    }

    @PostMapping("/signIn")
    public String postSignIn() throws Exception {
        return "redirect:/user/signIn";
    }

    @GetMapping("/signUp")
    public String signUp() {
        return "signUp";
    }

    @PostMapping("/signUp")
    public String postSignUp(@Validated UserSignUpRequest request) throws Exception {
        User user = userService.signUp(request);
        return "redirect:/user/signIn";
    }
}
