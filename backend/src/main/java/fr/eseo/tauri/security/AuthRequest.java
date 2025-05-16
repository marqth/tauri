package fr.eseo.tauri.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
@AllArgsConstructor
public class AuthRequest {
    private String login;
    private String password;
}
