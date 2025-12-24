package com.projectManagementTool;

import java.sql.Date;

import javax.crypto.SecretKey;

import java.security.Key;

import org.springframework.security.core.Authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class JwtProvider {

    // Define a static key
//    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
	private static final SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());
//    public static String generateToken(Authentication authentication) {
//        String jwt = Jwts.builder()
//                .setIssuedAt(new Date(0))
//                .setExpiration(new Date(new Date(0).getTime() + 86400000)) // 1 day expiry
//                .claim("email", authentication.getName()) // Replace with actual email getter
//                .signWith(key)
//                .compact();
//
//        return jwt;
//    }
	
	public static String generateToken(Authentication authentication, String secret) {
	    Key key = Keys.hmacShaKeyFor(secret.getBytes());

	    return Jwts.builder()
	            .setIssuedAt(new Date(System.currentTimeMillis()))
	            .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day
	            .claim("email", authentication.getName())
	            .signWith(key, SignatureAlgorithm.HS256)
	            .compact();
	}
    
    public static String generatePasswordResetToken(String email, String secret) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15)) // 15 mins
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    // public static String getEmailFromToken(String token, String secret) {
    //     Claims claims = Jwts.parserBuilder()
    //             .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
    //             .build()
    //             .parseClaimsJws(token)
    //             .getBody();
    //     return claims.getSubject();
    // }
	
	public static String getEmailFromToken(String jwt, String secret) {
	    jwt = jwt.substring(7);
	    Key key = Keys.hmacShaKeyFor(secret.getBytes());
	    Claims claims = Jwts.parserBuilder()
	            .setSigningKey(key)
	            .build()
	            .parseClaimsJws(jwt)
	            .getBody();
	    return String.valueOf(claims.get("email"));
	}
	
	
}
