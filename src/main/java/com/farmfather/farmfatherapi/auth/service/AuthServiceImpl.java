package com.farmfather.farmfatherapi.auth.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.farmfather.farmfatherapi.auth.dto.LoginRequestDto;
import com.farmfather.farmfatherapi.auth.dto.RegisterRequestDto;
import com.farmfather.farmfatherapi.auth.dto.UserResponseDto;
import com.farmfather.farmfatherapi.auth.entity.CustomUserDetails;
import com.farmfather.farmfatherapi.auth.exception.AlreadyExistEmailException;
import com.farmfather.farmfatherapi.cloud.s3.service.S3Service;
import com.farmfather.farmfatherapi.utils.EsRequestFactory;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

	private final AuthenticationManager authenticationManager;
	private final JwtUserDetailsService userDetailsService;
	private final RestHighLevelClient esClient;
	private final S3Service s3Service;


	@Value("${elasticsearch.index.user.name}")
	private String USER_INDEX;

	@Value("${cloud.aws.s3.bucket.user_profile}")
	private String USER_PROFILE_BUCKET;

	public AuthServiceImpl(AuthenticationManager authenticationManager,
			JwtUserDetailsService userDetailsService, S3Service s3Service, RestHighLevelClient esClient) {
		this.authenticationManager = authenticationManager;
		this.userDetailsService = userDetailsService;
		this.s3Service = s3Service;
		this.esClient = esClient;
	}

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
	public UserResponseDto register(RegisterRequestDto registerRequestDto) {
		CustomUserDetails user = new CustomUserDetails();
		user.setEmail(registerRequestDto.getEmail());
		user.setPassword(BCrypt.hashpw(registerRequestDto.getPassword(), BCrypt.gensalt()));
		user.setNickName(registerRequestDto.getNickName());

		CustomUserDetails savedUser;
		try {
			savedUser = userDetailsService.save(user);
		} catch (AlreadyExistEmailException e) {
			log.error("email already exists. email=" + registerRequestDto.getEmail(), e);
			return null;
		}

		return savedUser.toUserResponseDto();
	}

	@Override
	public String uploadProfile(String id, MultipartFile profileImage) {
		String profile = s3Service.uploadImage(USER_PROFILE_BUCKET, id, profileImage);
		log.info("image uploaded to s3 bucket. url=" + profile);

		String script = "";
		Map<String, Object> params = new HashMap<>();

		params.put("profile", profile);
		script += "ctx._source.profile = params.profile;";

		Script inline = new Script(ScriptType.INLINE, "painless", script, params);

		UpdateRequest request = EsRequestFactory.createUpdateWithScriptRequest(USER_INDEX, id, inline);
		try {
			esClient.update(request, RequestOptions.DEFAULT);
		} catch (IOException e) {
			log.error("IOException occured.");
			return null;
		}

		return profile;
	}
}
