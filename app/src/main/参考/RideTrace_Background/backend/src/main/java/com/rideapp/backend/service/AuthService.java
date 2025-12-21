package com.rideapp.backend.service;

import com.rideapp.backend.domain.User;
import com.rideapp.backend.dto.AuthResultDTO;
import com.rideapp.backend.dto.LoginRequestDTO;
import com.rideapp.backend.dto.RegisterRequestDTO;
import com.rideapp.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 登录：根据用户名查 users 表，比较密码。
     */
    public AuthResultDTO login(LoginRequestDTO req) {
        String idOrEmail = req.getUsername();
        Optional<User> optionalUser = idOrEmail != null && idOrEmail.contains("@")
                ? userRepository.findByEmail(idOrEmail)
                : userRepository.findByUsername(idOrEmail);
        if (optionalUser.isEmpty()) {
            return null;
        }
        User user = optionalUser.get();

        if (!user.getPassword().equals(req.getPassword())) {
            return null;
        }

        String token = "fake-token-" + user.getId();

        return new AuthResultDTO(
                user.getId(),
                user.getUsername(),
                user.getUsername(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getBio(),
                user.getGender(),
                token
        );
    }

    /**
     * 注册：检查用户名是否存在，不存在则插入一条记录。
     */
    @Transactional
    public AuthResultDTO register(RegisterRequestDTO req) {
        String value = req.getUsername();
        boolean isEmail = value != null && value.contains("@");

        boolean exists = isEmail ? userRepository.existsByEmail(value) : userRepository.existsByUsername(value);
        if (exists) {
            return null;
        }

        User user = new User();
        user.setUsername(value);
        if (isEmail) {
            user.setEmail(value);
        }
        user.setPassword(req.getPassword());

        User saved = userRepository.save(user);

        String token = "fake-token-" + saved.getId();
        return new AuthResultDTO(
                saved.getId(),
                saved.getUsername(),
                saved.getUsername(),
                saved.getEmail(),
                saved.getAvatarUrl(),
                saved.getBio(),
                saved.getGender(),
                token
        );
    }
}
