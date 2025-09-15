package com.auth.authApp.service;


import com.auth.authApp.entity.UserEntity;
import com.auth.authApp.exceptions.EmailAlreadyExistsException;
import com.auth.authApp.exceptions.UserNotFloundException;
import com.auth.authApp.io.ProfileRequestDTO;
import com.auth.authApp.io.ProfileResponseDTO;
import com.auth.authApp.mapper.ProfileMapper;
import com.auth.authApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;


@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService{
    private final UserRepository userRepository;
    private final ProfileMapper profileMapper;
    private final EmailService emailService;

//    public ProfileServiceImpl(UserRepository userRepository){
//        this.userRepository = userRepository;
//    }
    @Override
    public ProfileResponseDTO createProfile(ProfileRequestDTO request) {
        UserEntity newProfile = profileMapper.convertToUserEntity(request);
        if(!userRepository.existsByEmail(request.getEmail())){
            newProfile = userRepository.save(newProfile);
            return ProfileMapper.convertToProfileResponse(newProfile);
        }
//        throw new ResponseStatusException(HttpStatus.CONFLICT,"Email already exists");
        throw new EmailAlreadyExistsException("User with this email already exists " + request.getEmail());
    }

    @Override
    public ProfileResponseDTO getProfile(String email) {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFloundException("User with email " + email + " not found"));
        return ProfileMapper.convertToProfileResponse(userEntity);
    }

    @Override
    public void sendResetOtp(String email) {
        UserEntity existingUser = userRepository.findByEmail(email)
                .orElseThrow(()-> new UserNotFloundException("User with email " + email + " not found"));
        // generate 6 digit otp
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000,1000000));

        //set expire time for the otp
        long expireTime = System.currentTimeMillis() + (10 * 60 * 1000);

        existingUser.setResetOtp(otp);
        existingUser.setResetOtpExpireAt(expireTime);

        userRepository.save(existingUser);

        try {
            // send the reset otp email
            emailService.sendResetOtpEmail(existingUser.getEmail(),otp);
        }catch(Exception ex){
            throw new RuntimeException("Unable to send email");
        }
    }

    @Override
    public void resetPassword(String email, String otp, String newPassword) {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(()-> new UserNotFloundException("User with email " + email + " not found"));

        if(userEntity.getResetOtp() == null || !userEntity.getResetOtp().equals(otp)){
            throw new RuntimeException("Invalid OTP");
        }

        if(userEntity.getResetOtpExpireAt() < System.currentTimeMillis()){
            throw new RuntimeException("OTP Expired");
        }

        userEntity.setPassword(newPassword);
        userEntity.setResetOtp(null);
        userEntity.setResetOtpExpireAt(0L);

        userRepository.save(userEntity);
    }

    @Override
    public void sendEmailVerifyOtp(String email) {
        UserEntity existingUser = userRepository.findByEmail(email)
                .orElseThrow(()-> new UserNotFloundException("User with email " + email + " not found"));

        if(existingUser.getIsAccountVerified() != null && existingUser.getIsAccountVerified()){
            throw new RuntimeException("Account already verified");
        }

        //Generate 6 digit otp
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000,1000000));

        //set expire time for the otp
        long expireTime = System.currentTimeMillis() + (10 * 60 * 1000);

        existingUser.setVerifyOtp(otp);
        existingUser.setVerifyOtpExpireAt(expireTime);

        userRepository.save(existingUser);

        try {
            // send the reset otp email
            emailService.sendEmailVerificationOtp(existingUser.getEmail(),otp);
        }catch(Exception ex) {
            throw new RuntimeException("Unable to send email");
        }
    }

    @Override
    public void verifyEmailverificationOtp(String email, String otp) {
        UserEntity existingUser = userRepository.findByEmail(email)
                .orElseThrow(()-> new UserNotFloundException("User with email " + email + " not found"));

        if(existingUser.getVerifyOtp() == null || !existingUser.getVerifyOtp().equals(otp)){
            throw new RuntimeException("Invalid OTP");
        }

        if(existingUser.getVerifyOtpExpireAt() < System.currentTimeMillis()){
            throw new RuntimeException("OTP Expired");
        }

        existingUser.setIsAccountVerified(true);
        existingUser.setVerifyOtp(null);
        existingUser.setVerifyOtpExpireAt(0L);

        userRepository.save(existingUser);
    }
    
}
