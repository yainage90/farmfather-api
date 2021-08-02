package com.farmfather.farmfatherapi.auth.service;

import java.util.Base64;
import java.util.Date;

import com.farmfather.farmfatherapi.auth.exception.JwtException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtil {

    private static String JWT_SECRET;

    private static long EXPIRATION_TIME;

    public static String generateToken(UserDetails userDetails) {

        Date now = new Date();

        return Jwts.builder()
                    .setSubject(userDetails.getUsername())
                    .setIssuedAt(now)
                    .setExpiration(new Date(now.getTime() + EXPIRATION_TIME))
                    .signWith(SignatureAlgorithm.HS256, Base64.getEncoder().encodeToString(JWT_SECRET.getBytes()))
                    .compact();
    }

    public static String validate(String jwt) throws JwtException {

        String userId;
        try {
            userId = Jwts.parser()
                        .setSigningKey(Base64.getEncoder().encodeToString(JWT_SECRET.getBytes()))
                        .parseClaimsJws(jwt).getBody().getSubject();

        } catch (SignatureException e) { 
            log.error("Invalid JWT signature.\n" + jwt, e.getMessage()); 
            throw new JwtException("Invalid JWT signature");
        } catch (MalformedJwtException e) { 
            log.error("Invalid JWT token\n" + jwt, e.getMessage()); 
            throw new JwtException("Invalid JWT token");
        } catch (ExpiredJwtException e) { 
            log.error("JWT token is expired\n" + jwt, e.getMessage()); 
            throw new JwtException("JWT token is expired");
        } catch (UnsupportedJwtException e) { 
            log.error("JWT token is unsupported\n" + jwt, e.getMessage()); 
            throw new JwtException("JWT token is unsupported");
        } catch (IllegalArgumentException e) { 
            log.error("JWT claims string is empty\n" + jwt, e.getMessage()); 
            throw new JwtException("JWT claims string is empty");
        }

        if(!StringUtils.hasLength(jwt)) {
            throw new JwtException("Jwt is null or empty");
        }

        return userId;
    }

    @Value("${jwt.secret}")
    public void setSecretKey(String value) {
        JWT_SECRET = value;
    }

    @Value("${jwt.token-validity-in-days}")
    public void setExpirationTime(long value) {
        EXPIRATION_TIME = value * 24 * 60 * 60 * 1000;
    }
    
}
