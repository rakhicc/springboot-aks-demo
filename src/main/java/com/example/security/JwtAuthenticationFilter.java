package com.example.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
/*What is JwtAuthenticationFilter?
This class is a Spring Security filter that:
        ✅ Intercepts every incoming HTTP request
✅ Extracts JWT token from the request
✅ Validates the token
✅ Authenticates the user
👉 In simple words:
        “It checks if the incoming request has a valid JWT token and logs the user in automatically.”

        ⏰ When is this class invoked?
        ✅ Answer: On every HTTP request
Specifically:

Every request goes through the Spring Security filter chain
Your filter extends:

JavaOncePerRequestFilterShow more lines
👉 This guarantees:
        ✅ It runs once per request

🔄 Example flow
1. Client calls API
HTTPGET /api/productsAuthorization: Bearer eyJhbGciOiJIUzI1Ni...Show more lines

2. Spring Security pipeline
Request → Security Filter Chain → JwtAuthenticationFilter → Controller

👉 Your filter runs before the controller*/

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("filterinternal");
        String header = request.getHeader("Authorization");
        String path = request.getServletPath();
        if (path.startsWith("/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            System.out.println("token is "+token);
            if (jwtService.isValid(token)) {
                String username = jwtService.extractUsername(token);

                var auth = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        Collections.emptyList()
                );

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}