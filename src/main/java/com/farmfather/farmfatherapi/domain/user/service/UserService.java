package com.farmfather.farmfatherapi.domain.user.service;

import com.farmfather.farmfatherapi.auth.entity.CustomUserDetails;

public interface UserService {

	CustomUserDetails getById(String id);

	CustomUserDetails getByEmail(String email);

	CustomUserDetails save(CustomUserDetails user);

	String deleteCustomUserDetails(String id);

	CustomUserDetails changeNickName(String id, String nickName);
}
