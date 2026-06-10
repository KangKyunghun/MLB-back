package com.mlb.mlb_back.Domain.user.service;

import com.mlb.mlb_back.Domain.user.dto.*;
import com.mlb.mlb_back.Domain.user.entity.User;
import com.mlb.mlb_back.Domain.user.repository.UserRepository;
import com.mlb.mlb_back.Global.exception.ApiException;
import com.mlb.mlb_back.Global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final StringRedisTemplate redisTemplate;

    private static final String REFRESH_TOKEN_PREFIX = "refresh:";
    private static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(14);

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다");
        }
        if (userRepository.findByNickname(request.getNickname()).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .role("USER")
                .build();

        User saved = userRepository.save(user);

        return SignupResponse.builder()
                .id(saved.getId())
                .email(saved.getEmail())
                .nickname(saved.getNickname())
                .build();
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다");
        }

        String accessToken = jwtProvider.createAccessToken(user.getEmail(), user.getRole());
        String refreshToken = jwtProvider.createRefreshToken(user.getEmail());

        // Redis에 refresh token 저장
        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + user.getEmail(),
                refreshToken,
                REFRESH_TOKEN_TTL
        );

        return LoginResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getRole())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public TokenResponse refresh(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken) || !jwtProvider.isRefreshToken(refreshToken)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다");
        }

        String email = jwtProvider.getEmail(refreshToken);

        // Redis에 저장된 토큰과 비교
        String stored = redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + email);
        if (!refreshToken.equals(stored)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 일치하지 않습니다");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다"));

        String newAccessToken = jwtProvider.createAccessToken(user.getEmail(), user.getRole());
        String newRefreshToken = jwtProvider.createRefreshToken(user.getEmail());

        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + email,
                newRefreshToken,
                REFRESH_TOKEN_TTL
        );

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    public boolean checkEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean checkNicknameDuplicate(String nickname) {
        return userRepository.findByNickname(nickname).isPresent();
    }
}