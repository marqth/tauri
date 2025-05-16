package fr.eseo.tauri.service;

import fr.eseo.tauri.model.Project;
import fr.eseo.tauri.model.User;
import fr.eseo.tauri.repository.ProjectRepository;
import fr.eseo.tauri.repository.UserRepository;
import fr.eseo.tauri.security.AuthResponse;
import fr.eseo.tauri.security.JwtTokenUtil;
import fr.eseo.tauri.util.CustomLogger;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    @Getter
    @Setter
    @Value("${app.log.with.ldap}")
    private String prodProperty;

    private static final String WRONG_CREDENTIALS = "Wrong credentials";

    public AuthResponse login(String email, String password) {
        try {
            User user;

            if(prodProperty.equals("true")){       // Auth with LDAP
                Authentication authentication = authenticate(email, password);
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();

                user = userRepository.findByEmail(userDetails.getUsername())
                        .orElseThrow(() -> new SecurityException(WRONG_CREDENTIALS));
            } else {                               // Auth without LDAP for dev mode
                user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new SecurityException(WRONG_CREDENTIALS));
            }

            String accessToken = jwtTokenUtil.generateAccessToken(user);
            CustomLogger.info("Access token generated for user " + user.id() + " : " + accessToken);
            Integer idProject = projectRepository.findFirstByActualTrue().map(Project::id).orElse(0);
            return new AuthResponse(user.id(), accessToken, idProject);
        } catch (Exception e){
            throw new SecurityException(WRONG_CREDENTIALS + e.getMessage());
        }
    }

    public Authentication authenticate(String email, String password) {
        String safeEmail = StringEscapeUtils.escapeHtml4(email);
        String safePassword = StringEscapeUtils.escapeHtml4(password);
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(safeEmail, safePassword)
        );
    }

}
