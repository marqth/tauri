package fr.eseo.tauri.security;

import fr.eseo.tauri.util.CustomLogger;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        String token = jwtTokenUtil.extractToken(request);

        CustomLogger.info(request.getRequestURI());

        if (token != null && jwtTokenUtil.validateAccessToken(token)) {
            UserDetails userDetails = jwtTokenUtil.createUserDetails(token);
            jwtTokenUtil.setAuthenticationContext(userDetails, request);
            filterChain.doFilter(request, response);

        } else if (request.getRequestURI().equals("/api/auth/login") || request.getRequestURI().equals("/tauri/api/auth/login")) {
            filterChain.doFilter(request, response);

        } else if (request.getRequestURI().contains(("/api/")) || request.getRequestURI().contains("/tauri/api/")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        } else {
            filterChain.doFilter(request, response);
        }
    }
}
