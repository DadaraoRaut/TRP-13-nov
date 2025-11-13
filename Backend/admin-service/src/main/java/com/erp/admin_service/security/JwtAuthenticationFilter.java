package com.erp.admin_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        System.out.println("➡️ Request to: " + request.getRequestURI());
        System.out.println("🔐 Authorization Header: " + authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String username = jwtUtil.extractUsername(token);
                System.out.println("✅ Extracted username from token: " + username);

                if (username != null && jwtUtil.validateToken(token, username)) {
                    var auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                            username, null, java.util.Collections.emptyList());
                    org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(auth);
                    System.out.println("✅ Token validated and SecurityContext set");
                } else {
                    System.out.println("❌ Invalid or expired token for user: " + username);
                }
            } catch (Exception e) {
                System.out.println("❌ JWT validation failed: " + e.getMessage());
            }
        } else {
            System.out.println("⚠️ No Authorization header found");
        }

        chain.doFilter(request, response);
    }




}
