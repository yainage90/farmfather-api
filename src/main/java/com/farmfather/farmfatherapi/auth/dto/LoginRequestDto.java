package com.farmfather.farmfatherapi.auth.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoginRequestDto {
    private String email;
    private String password;
}
