package com.farmfather.farmfatherapi.auth.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.farmfather.farmfatherapi.auth.exception.JwtException;
import com.farmfather.farmfatherapi.auth.service.JwtUserDetailsService;
import com.farmfather.farmfatherapi.auth.service.JwtUtil;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUserDetailsService jwtUserDetailsService;



	@Override
	public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

		final String jwt = request.getHeader("jwt");

		String email = null;

		if(jwt != null) {
			try {
				email = JwtUtil.validate(jwt);
				request.setAttribute("email", email);
			} catch(JwtException e) {
				sendError(HttpStatus.UNAUTHORIZED, response);
				return;
			}
		} 

		if(email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(email);
			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = 
			new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
      usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails((HttpServletRequest)request));

			SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
		}

		chain.doFilter(request, response);

	}

	private void sendError(HttpStatus status, HttpServletResponse res) throws IOException {
		res.setStatus(status.value());
        res.setContentType("application/json");
		res.sendError(status.value(), "UNAUTHORIZED");
	}
}
