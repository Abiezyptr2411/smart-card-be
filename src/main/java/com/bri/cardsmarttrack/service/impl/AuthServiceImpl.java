package com.bri.cardsmarttrack.service.impl;

import com.bri.cardsmarttrack.dto.request.LoginRequest;
import com.bri.cardsmarttrack.dto.response.JwtAuthResponse;
import com.bri.cardsmarttrack.dto.response.UserProfileResponse;
import com.bri.cardsmarttrack.security.JwtTokenProvider;
import com.bri.cardsmarttrack.security.UserPrincipal;
import com.bri.cardsmarttrack.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Override
    public JwtAuthResponse login(LoginRequest request) {
        log.info("Authenticating customer with email: {}", request.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().trim().toLowerCase(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        UserProfileResponse profile = UserProfileResponse.builder()
                .id(userPrincipal.getId())
                .email(userPrincipal.getEmail())
                .fullName(userPrincipal.getFullName())
                .build();

        return JwtAuthResponse.builder()
                .token(jwt)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getExpirationInMs())
                .customer(profile)
                .build();
    }

    @Override
    public UserProfileResponse getCurrentUser(UserPrincipal principal) {
        return UserProfileResponse.builder()
                .id(principal.getId())
                .email(principal.getEmail())
                .fullName(principal.getFullName())
                .build();
    }
}
