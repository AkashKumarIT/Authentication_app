package com.auth.authApp.controller;

import com.auth.authApp.exceptions.AccountDisabledException;
import com.auth.authApp.exceptions.AuthenticationFailedException;
import com.auth.authApp.exceptions.EmailOrPasswordIncorrectException;
import com.auth.authApp.io.AuthRequestDTO;
import com.auth.authApp.io.AuthResponseDTO;
import com.auth.authApp.service.AppUserDetailsService;
import com.auth.authApp.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final AppUserDetailsService appUserDetailsService;
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

}
