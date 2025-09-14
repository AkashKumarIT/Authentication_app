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


@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService{
    private final UserRepository userRepository;
    private final ProfileMapper profileMapper;

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
}
