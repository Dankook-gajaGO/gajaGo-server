package com.example.gazago.gazago.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignupRequestDto {
    private String loginId;
    private String password;
    private String nickname;
    private String email;
}