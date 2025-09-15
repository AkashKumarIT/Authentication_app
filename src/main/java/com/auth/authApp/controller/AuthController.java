package com.auth.authApp.controller;

import com.auth.authApp.exceptions.AccountDisabledException;
import com.auth.authApp.exceptions.AuthenticationFailedException;
import com.auth.authApp.exceptions.EmailOrPasswordIncorrectException;
import com.auth.authApp.io.AuthRequestDTO;
import com.auth.authApp.io.AuthResponseDTO;
import com.auth.authApp.io.ResetPasswordRequest;
import com.auth.authApp.service.AppUserDetailsService;
import com.auth.authApp.service.ProfileService;
import com.auth.authApp.service.ProfileServiceImpl;
import com.auth.authApp.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final AppUserDetailsService appUserDetailsService;
    private final ProfileService profileService;
    private final JwtUtil jwtUtil;
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO request){
        try{
            authenticate(request.getEmail(),request.getPassword());
            final UserDetails userDetails = appUserDetailsService.loadUserByUsername(request.getEmail());
            final String jwtToken = jwtUtil.generateToken(userDetails);
            ResponseCookie cookie = ResponseCookie.from("jwt",jwtToken)
                    .httpOnly(true)
                    .path("/")
                    .maxAge(Duration.ofHours(1))
                    .sameSite("Strict")
                    .build();
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,cookie.toString())
                    .body(new AuthResponseDTO(request.getEmail(),jwtToken));
        }catch(BadCredentialsException ex){
            throw new EmailOrPasswordIncorrectException("Email or Password is incorrect");
        }catch(DisabledException ex){
            throw new AccountDisabledException("Account is disabled");
        }catch(Exception ex){
            throw new AuthenticationFailedException("Authentication failed");
        }
    }

    private void authenticate(String email,String password){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email,password));
    }

    @GetMapping("/is-authenticated")
    public ResponseEntity<Boolean> isAuthenticated(@CurrentSecurityContext(expression = "authentication?.name") String email){
        return ResponseEntity.ok(email != null);
    }

    @GetMapping("/send-reset-otp")
    public void sendResetOtp(@RequestParam String email){
        try {
            profileService.sendResetOtp(email);
        }catch(Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public void resetPassword(@Valid @RequestBody ResetPasswordRequest request){
        try{
            profileService.resetPassword(request.getEmail(),request.getOtp(),request.getNewPassword());
        }catch(Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping("/send-verify-email-otp")
    public void sendVerifyEmailOtp(@CurrentSecurityContext(expression = "authentication?.name") String email){
        try {
            profileService.sendEmailVerifyOtp(email);
        }catch(Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

    }

    @PostMapping("/verify-email")
    public void verifyEmail(@RequestBody Map<String,Object> request, @CurrentSecurityContext(expression = "authentication?.name") String email){

        if(request.get("otp").toString() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing Details");
        }
        try{
            profileService.verifyEmailverificationOtp(email,request.get("otp").toString());
        }catch(Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}
