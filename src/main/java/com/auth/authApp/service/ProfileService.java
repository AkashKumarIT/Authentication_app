package com.auth.authApp.service;

import com.auth.authApp.io.ProfileRequestDTO;
import com.auth.authApp.io.ProfileResponseDTO;

public interface ProfileService {
    ProfileResponseDTO createProfile(ProfileRequestDTO request);
}
