package fr.eseo.tauri.security;

import fr.eseo.tauri.model.User;
import fr.eseo.tauri.util.CustomLogger;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenUtil {

    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.issuer}")
    private String issuer;

    @Value("${app.jwt.expiration}")
    private long expireDuration;

    public String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        return (header != null && header.startsWith("Bearer ")) ? header.substring(7) : null;
    }

    public boolean validateAccessToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            CustomLogger.error("Validate token : " + e);
            return false;
        }
    }

    public UserDetails createUserDetails(String token) {
        User userDetails = new User();
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        userDetails.email(claims.getSubject().split(",")[0]);
        return userDetails;
    }

    public void setAuthenticationContext(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .setSubject(String.format("%s", user.email()))
                .setIssuer(issuer)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plusMillis(expireDuration)))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
}
