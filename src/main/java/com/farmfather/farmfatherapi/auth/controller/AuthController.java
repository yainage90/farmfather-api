package com.farmfather.farmfatherapi.auth.controller;

import com.farmfather.farmfatherapi.auth.dto.LoginRequestDto;
import com.farmfather.farmfatherapi.auth.dto.LoginResponseDto;
import com.farmfather.farmfatherapi.auth.dto.RegisterRequestDto;
import com.farmfather.farmfatherapi.auth.entity.CustomUserDetails;
import com.farmfather.farmfatherapi.auth.service.AuthService;
import com.farmfather.farmfatherapi.auth.service.JwtUserDetailsService;
import com.farmfather.farmfatherapi.auth.service.JwtUtil;
import com.farmfather.farmfatherapi.domain.user.dto.UserResponseDto;
import com.farmfather.farmfatherapi.domain.user.exception.AlreadyExistEmailException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final JwtUserDetailsService userDetailsService;
    private final AuthService authService;

    @PostMapping("/api/authenticate")
    public ResponseEntity<LoginResponseDto> createAuthenticationToken(
            @RequestBody LoginRequestDto loginRequestDto) throws Exception {

        authService.authenticate(loginRequestDto);

        final CustomUserDetails userDetails = (CustomUserDetails) userDetailsService
                .loadUserByUsername(loginRequestDto.getEmail());
        final String jwt = JwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new LoginResponseDto(userDetails.toUserResponseDto(), jwt));
    }

    @PostMapping("/api/register")
    public ResponseEntity<UserResponseDto> register(
            @RequestBody RegisterRequestDto registerRequestDto) throws Exception {

        UserResponseDto response;
        try {
            response = authService.register(registerRequestDto);
        } catch (AlreadyExistEmailException e) {
            log.error("email already exists. email=" + registerRequestDto.getEmail(), e);
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
        return ResponseEntity.ok(response);
    }

}
