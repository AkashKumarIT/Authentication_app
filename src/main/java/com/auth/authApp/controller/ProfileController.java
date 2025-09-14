package com.auth.authApp.controller;

import com.auth.authApp.io.ProfileRequestDTO;
import com.auth.authApp.io.ProfileResponseDTO;
import com.auth.authApp.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<ProfileResponseDTO> createProfile(@Valid @RequestBody ProfileRequestDTO profileRequest){
        ProfileResponseDTO profileResponse = profileService.createProfile(profileRequest);
        return ResponseEntity.ok().body(profileResponse);
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponseDTO> getProfile(@CurrentSecurityContext(expression = "authentication?.name")String email){

    }

}
