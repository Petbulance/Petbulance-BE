package com.example.Petbulance_BE.global.util;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {

    private final String secretKey;
    private final int expiration;
    private SecretKey SecretKEY;


    public JWTUtil(@Value("${spring.jwt.secret}") String secretKey, @Value("${spring.jwt.expiration}") int expiration){
        this.secretKey = secretKey;
        this.expiration = expiration;
        this.SecretKEY = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }
    //jwt토큰 생성
    public String createJwt(String userId, String category, String role, String provider) {
        return Jwts.builder()
                .claim("userId", userId)
                .claim("category", category)
                .claim("role", role)
                .claim("provider", provider)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration*60*1000L))
                .signWith(SecretKEY)
                .compact();
    }
    public String createAdminJwt(String userId, String category, String role) {
        return Jwts.builder()
                .claim("userId", userId)
                .claim("category", category)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration*60*1000L))
                .signWith(SecretKEY)
                .compact();
    }
    //userId추출
    public String getUserId(String accessToken) {
        return Jwts.parser()
                .verifyWith(SecretKEY)
                .build()
                .parseSignedClaims(accessToken)
                .getPayload()
                .get("userId", String.class);
    }
    //역할(role)추출
    public String getRole(String accessToken) {
        return Jwts.parser()
                .verifyWith(SecretKEY)
                .build()
                .parseSignedClaims(accessToken)
                .getPayload()
                .get("role", String.class);
    }
    //provider 추출
    public String getProvider(String accessToken) {
        return Jwts.parser()
                .verifyWith(SecretKEY)
                .build()
                .parseSignedClaims(accessToken)
                .getPayload()
                .get("provider", String.class);
    }
    //만료 여부 확인
    public Boolean isExpired(String accessToken) {
        return Jwts.parser()
                .verifyWith(SecretKEY)
                .build()
                .parseSignedClaims(accessToken)
                .getPayload()
                .getExpiration()
                .before(new Date());
    }
    //만료 시간 추출
    public Date getExpiration(String accessToken) {
        return Jwts.parser()
                .verifyWith(SecretKEY)
                .build()
                .parseSignedClaims(accessToken)
                .getPayload()
                .getExpiration();
    }

    public String appCreateJwt(String userId, String category, String role, String provider) {
        return Jwts.builder()
                .claim("userId", userId)
                .claim("category", category)
                .claim("role", role)
                .claim("provider", provider)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration*60*10000000L))
                .signWith(SecretKEY)
                .compact();
    }
}
