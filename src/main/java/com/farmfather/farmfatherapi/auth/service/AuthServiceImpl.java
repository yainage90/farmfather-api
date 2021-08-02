package com.farmfather.farmfatherapi.auth.service;

import com.farmfather.farmfatherapi.auth.dto.LoginRequestDto;
import com.farmfather.farmfatherapi.auth.dto.RegisterRequestDto;
import com.farmfather.farmfatherapi.auth.entity.CustomUserDetails;
import com.farmfather.farmfatherapi.domain.user.dto.UserResponseDto;
import com.farmfather.farmfatherapi.domain.user.exception.AlreadyExistEmailException;
import com.farmfather.farmfatherapi.domain.user.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final AuthenticationManager authenticationManager;
	private final UserService userService;

	@Override
	public void authenticate(LoginRequestDto loginRequestDto) throws Exception {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					loginRequestDto.getEmail(), loginRequestDto.getPassword()));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
	}

	@Override
	public UserResponseDto register(RegisterRequestDto registerRequestDto)
			throws AlreadyExistEmailException {
		CustomUserDetails user = new CustomUserDetails();
		user.setEmail(registerRequestDto.getEmail());
		user.setPassword(BCrypt.hashpw(registerRequestDto.getPassword(), BCrypt.gensalt()));
		user.setNickName(registerRequestDto.getNickName());

		return userService.save(user).toUserResponseDto();
	}

}
