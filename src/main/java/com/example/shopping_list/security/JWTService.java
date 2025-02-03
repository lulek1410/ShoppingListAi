package com.example.shopping_list.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.shopping_list.user.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTService {

  @Value("${jwt.secret}") private String secret;

  public String createToken(User user) {
    return Jwts.builder()
      .subject(user.getId().toString())
      .claim("email", user.getEmail())
      .issuedAt(new Date())
      .issuer("shopping-list")
      .signWith(getSignKey())
      .compact();
  }

  public Long generateUserIdFromToken(String token) {
    Claims claims = Jwts.parser().verifyWith(getSignKey()).build().parseSignedClaims(token).getPayload();
    return Long.valueOf(claims.getSubject());
  }

  private SecretKey getSignKey() {
    byte[] secretBytes = Decoders.BASE64.decode(secret);
    return Keys.hmacShaKeyFor(secretBytes);
  }
}
