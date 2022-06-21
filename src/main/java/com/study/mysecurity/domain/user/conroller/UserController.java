package com.study.mysecurity.domain.user.conroller;

import com.study.mysecurity.domain.user.User;
import com.study.mysecurity.domain.user.dto.UserDetailResponse;
import com.study.mysecurity.domain.user.dto.UserSignUpRequest;
import com.study.mysecurity.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public String userList(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "user/userList";
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal User user, Model model) {
        UserDetailResponse userDetailResponse = new UserDetailResponse(user.getEmail(), user.getName());
        model.addAttribute("userDetail", userDetailResponse);
        return "user/profile";
    }

    @GetMapping("/signIn")
    public String signIn() {
        return "user/signIn";
    }

    @PostMapping("/signIn")
    public String postSignIn() throws Exception {
        return "redirect:/";
    }

    @GetMapping("/signUp")
    public String signUp() {
        return "user/signUp";
    }

    @PostMapping("/signUp")
    public String postSignUp(@ModelAttribute @Validated UserSignUpRequest request) throws Exception {
        User user = userService.signUp(request);
        return "redirect:/users/signIn";
    }
}
