package com.farmfather.farmfatherapi.auth.service;

import com.farmfather.farmfatherapi.auth.entity.CustomUser;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class JwtUserDetailService implements UserDetailsService {

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		CustomUser user = new CustomUser();
		user.setEmail("jchg90@naver.com");
		user.setPassword("1234");

		return user;
	}
}
