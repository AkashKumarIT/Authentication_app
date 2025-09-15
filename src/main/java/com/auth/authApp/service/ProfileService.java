package com.auth.authApp.service;

import com.auth.authApp.io.ProfileRequestDTO;
import com.auth.authApp.io.ProfileResponseDTO;

public interface ProfileService {
    ProfileResponseDTO createProfile(ProfileRequestDTO request);
    ProfileResponseDTO getProfile(String email);

    void sendResetOtp(String email);

    void resetPassword(String email,String otp,String newPassword);
}
