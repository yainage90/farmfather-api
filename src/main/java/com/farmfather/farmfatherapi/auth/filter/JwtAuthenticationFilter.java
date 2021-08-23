package com.farmfather.farmfatherapi.auth.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.farmfather.farmfatherapi.auth.exception.JwtException;
import com.farmfather.farmfatherapi.auth.service.JwtUserDetailsService;
import com.farmfather.farmfatherapi.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUserDetailsService jwtUserDetailsService;

	@Override
	public void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		log.info(request.getPathInfo());

		final String jwt = request.getHeader("jwt");

		String id = null;

		if (jwt != null) {
			try {
				id = JwtUtil.validate(jwt);
				request.setAttribute("id", id);
				log.info("jwt is valid. jwt=" + jwt);
			} catch (JwtException e) {
				log.error("authencation filter fail", e);
				sendError(HttpStatus.UNAUTHORIZED, response);
				return;
			}
		}

		if (id != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = jwtUserDetailsService.getById(id);
			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
					new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
			usernamePasswordAuthenticationToken.setDetails(
					new WebAuthenticationDetailsSource().buildDetails((HttpServletRequest) request));

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
