package com.auth.config;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.auth.entity.UserEntity;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.security.WeakKeyException;

@Component
public class JwtProvider {

	private static final Logger log = LoggerFactory.getLogger(JwtProvider.class);
	
	@Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;
    
    @Value("${security.jwt.company}")
    private String company;
	
	public String create(UserEntity user) {
		Map<String, Object> claims = Jwts.claims().setSubject(user.getUsername());
		claims.put("id",  user.getId());
		claims.put("company-name", company);
		
		Instant issuedAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
		Instant expiredAt = issuedAt.plus(jwtExpiration, ChronoUnit.MINUTES);
		
		return Jwts.builder()
				.setHeaderParam("typ", "JWT")
				.setClaims(claims)
				.setIssuedAt(Date.from(issuedAt))
				.setExpiration(Date.from(expiredAt))
				.signWith(getSignInKey(),SignatureAlgorithm.HS256)
				.compact();
	}
	
	public boolean validate(String token) {
			
			 try {
				 
				Jwts.parserBuilder()
				    .setSigningKey(getSignInKey())
				 	.build()
				 	.parseClaimsJws(token);
				
				return true;
				
			} catch (WeakKeyException e) {
				log.error("Weak key token : " , e.getMessage());
			} catch (SignatureException e) {
				log.error("Wrong signature token : " , e.getMessage());
			} catch (ExpiredJwtException e) {
				log.error("Expired token : " , e.getMessage());
			} catch (UnsupportedJwtException e) {
				log.error("Unsupported token : " , e.getMessage());
			} catch (MalformedJwtException e) {
				log.error("Malformed token : " , e.getMessage());
			} catch (IllegalArgumentException e) {
				log.error("Illegal token : " , e.getMessage());
			}
			 
		return false;
	}
	
	public String getUsernameFromToken(String token) {
		try {
			
			return Jwts.parserBuilder()
				    .setSigningKey(getSignInKey())
				 	.build()
				 	.parseClaimsJws(token)
				    .getBody().getSubject();
			
		}catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
		}
	}
	
	private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
	
	
}
