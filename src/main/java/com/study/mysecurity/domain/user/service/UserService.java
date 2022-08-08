package com.study.mysecurity.domain.user.service;

import com.study.mysecurity.domain.user.User;
import com.study.mysecurity.domain.user.UserDetailsAdapter;
import com.study.mysecurity.domain.user.dto.UserSignUpRequest;
import com.study.mysecurity.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        User user = optionalUser.orElseThrow(() -> new RuntimeException("해당 유저를 찾지 못하였습니다."));

        return new UserDetailsAdapter(user);
    }

    /**
     * 가입
     * @param  signUpReq 가입할 유저의 정보
     * @return 가입된 유저의 정보
     */
    @Transactional
    public User signUp(UserSignUpRequest signUpReq) throws Exception {
        System.out.println("signUpReq.toString() = " + signUpReq.toString());
        if (isEmailExist(signUpReq.getEmail())) {
            throw new Exception("중복된 이메일입니다.");
        }

        User newUser = signUpReq.toEntity();
        newUser.hashPassword(passwordEncoder);

        return userRepository.save(newUser);
    }

    /**
     * 로그인
     */

    /**
     * 탈퇴
     */

    /**
     * 유저 단건 조회
     */
    public User findUser(String email) {
        return userRepository.findByEmail(email).orElseGet(null);
    }

    /**
     * 모든 유저 조회
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    private boolean isEmailExist(String email) {
        Optional<User> byEmail = userRepository.findByEmail(email);
        return !byEmail.isEmpty();
    }
}
