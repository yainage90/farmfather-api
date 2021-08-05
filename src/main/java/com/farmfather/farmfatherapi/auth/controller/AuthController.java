package com.farmfather.farmfatherapi.auth.controller;

import javax.servlet.http.HttpServletRequest;
import com.farmfather.farmfatherapi.auth.dto.LoginRequestDto;
import com.farmfather.farmfatherapi.auth.dto.LoginResponseDto;
import com.farmfather.farmfatherapi.auth.dto.NickNameChangeRequestDto;
import com.farmfather.farmfatherapi.auth.dto.RegisterRequestDto;
import com.farmfather.farmfatherapi.auth.dto.UserResponseDto;
import com.farmfather.farmfatherapi.auth.entity.CustomUserDetails;
import com.farmfather.farmfatherapi.auth.exception.AlreadyExistEmailException;
import com.farmfather.farmfatherapi.auth.service.AuthService;
import com.farmfather.farmfatherapi.auth.service.JwtUserDetailsService;
import com.farmfather.farmfatherapi.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
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

        LoginResponseDto response = new LoginResponseDto(userDetails.toUserResponseDto(), jwt);
        log.info(response.toString());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/register")
    public ResponseEntity<UserResponseDto> register(
            @RequestBody RegisterRequestDto registerRequestDto) throws Exception {

        UserResponseDto response;
        try {
            response = authService.register(registerRequestDto);
        } catch (AlreadyExistEmailException e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/api/user/nickName")
    public ResponseEntity<UserResponseDto> changeNickName(
            @RequestBody NickNameChangeRequestDto nickNameChangeRequestDto,
            HttpServletRequest request) {
        UserResponseDto user =
                userDetailsService.changeNickName(request.getAttribute("id").toString(),
                        nickNameChangeRequestDto.getNickName()).toUserResponseDto();

        return ResponseEntity.ok(user);
    }

    @PostMapping("/api/user/profile")
    public ResponseEntity<String> uploadThumbnail(HttpServletRequest request,
            @RequestParam MultipartFile profileImage) {
        String id = request.getAttribute("id").toString();
        return ResponseEntity.ok(authService.uploadProfile(id, profileImage));
    }

    @GetMapping("/api/user/{id}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable String id) {
        return ResponseEntity.ok(userDetailsService.getById(id).toUserResponseDto());
    }
}
