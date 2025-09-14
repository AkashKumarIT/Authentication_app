package com.auth.authApp.mapper;

import com.auth.authApp.entity.UserEntity;
import com.auth.authApp.io.ProfileRequestDTO;
import com.auth.authApp.io.ProfileResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;
@RequiredArgsConstructor
@Component
public class ProfileMapper {
    private final PasswordEncoder passwordEncoder;
    public UserEntity convertToUserEntity(ProfileRequestDTO request){
        return UserEntity.builder()
                .userId(UUID.randomUUID().toString())
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .isAccountVerified(false)
                .resetOtp(null)
                .resetOtpExpireAt(0L)
                .verifyOtpExpireAt(0L)
                .build();
    }

    public static ProfileResponseDTO convertToProfileResponse(UserEntity user){
        return ProfileResponseDTO.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .isAccountVerified(user.getIsAccountVerified())
                .build();
    }
}
