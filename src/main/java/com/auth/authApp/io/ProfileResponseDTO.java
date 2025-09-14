package com.auth.authApp.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileResponseDTO {
    private String userId;
    private String name;
    private String email;
    private Boolean isAccountVerified;
}
