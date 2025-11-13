package com.erp.Employee.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    // Must be exactly the same as Auth Service SECRET_KEY
    private static final String SECRET_KEY = "mySuperSecretKey123456789012345678901234567890";

    /**
     * Extract employeeId (or username) from JWT token.
     */

    public String extractEmployeeId(String token) {
        try {
            if (token.startsWith("Bearer ")) token = token.substring(7);

            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY.getBytes())
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject(); // username/employeeId from Auth Service
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("JWT token expired", e);
        } catch (MalformedJwtException | SignatureException e) {
            throw new RuntimeException("Invalid JWT token", e);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing JWT token", e);
        }
    }

    /**
     * Optional: validate token (checks signature & expiration)
     */
    public boolean validateToken(String token) {
        try {
            extractEmployeeId(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
