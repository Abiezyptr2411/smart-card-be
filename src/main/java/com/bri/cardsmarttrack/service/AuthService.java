package com.bri.cardsmarttrack.service;

import com.bri.cardsmarttrack.dto.request.LoginRequest;
import com.bri.cardsmarttrack.dto.response.JwtAuthResponse;
import com.bri.cardsmarttrack.dto.response.UserProfileResponse;
import com.bri.cardsmarttrack.security.UserPrincipal;

public interface AuthService {
    JwtAuthResponse login(LoginRequest request);
    UserProfileResponse getCurrentUser(UserPrincipal principal);
}
