package fr.eseo.tauri.controller;

import fr.eseo.tauri.model.User;
import fr.eseo.tauri.security.AuthRequest;
import fr.eseo.tauri.security.AuthResponse;
import fr.eseo.tauri.service.AuthService;
import fr.eseo.tauri.util.CustomLogger;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request.login(), request.password());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logon")
    public Boolean logon(@RequestBody User user) {
        CustomLogger.info(user.email() + " is trying to log on");
        return true;
    }

    @PostMapping("/logout")
    public Boolean logout(@RequestBody User user) {
        CustomLogger.info(user.email() + " is trying to log out");
        return true;
    }


}
