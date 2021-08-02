package com.farmfather.farmfatherapi.auth.entity;

import java.util.Collection;
import java.util.List;
import com.farmfather.farmfatherapi.domain.user.dto.UserResponseDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class CustomUserDetails implements UserDetails {

	private String id;
	private String email;
	private String nickName;
	private String password;
	private String introduce;
	private String imageUrl;
	private List<String> favoriteCourses;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public UserResponseDto toUserResponseDto() {
		return new UserResponseDto(getId(), getUsername(), getNickName(), getIntroduce(), getImageUrl(),
				getFavoriteCourses());
	}
}
