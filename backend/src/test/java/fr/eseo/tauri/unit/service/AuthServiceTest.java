package fr.eseo.tauri.unit.service;


import fr.eseo.tauri.model.Project;
import fr.eseo.tauri.model.User;
import fr.eseo.tauri.repository.ProjectRepository;
import fr.eseo.tauri.repository.UserRepository;
import fr.eseo.tauri.security.AuthResponse;
import fr.eseo.tauri.security.JwtTokenUtil;
import fr.eseo.tauri.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Nested
class AuthServiceTest {

    @InjectMocks
    AuthService authService;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    UserRepository userRepository;

    @Mock
    ProjectRepository projectRepository;

    @Mock
    JwtTokenUtil jwtTokenUtil;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should throw SecurityException when credentials are incorrect")
    void shouldThrowSecurityExceptionWhenCredentialsAreIncorrect() {
        String email = "john.doe@example.com";
        String password = "wrongpassword";

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Wrong credentials"));

        assertThrows(SecurityException.class, () -> authService.login(email, password));
    }

    @Test
    @DisplayName("Should return Authentication when credentials are correct")
    void shouldReturnAuthenticationWhenCredentialsAreCorrect() {
        String email = "john.doe@example.com";
        String password = "password";
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        Authentication actualAuthentication = authService.authenticate(email, password);

        assertNotNull(actualAuthentication);
    }

    @Test
    @DisplayName("Should throw SecurityException when user is not found")
    void shouldReturnAuthResponseWhenCredentialsAreCorrectAndLdapEnabled() {
        String email = "john.doe@example.com";
        String password = "password";
        Authentication authentication = mock(Authentication.class);
        User user = new User();
        user.email(email);
        Project project = new Project();
        project.id(1);
        authService.prodProperty("true");

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(jwtTokenUtil.generateAccessToken(user)).thenReturn("accessToken");
        when(projectRepository.findFirstByActualTrue()).thenReturn(Optional.of(project));
        when(userRepository.findByEmail(email)).thenReturn(null);

        assertThrows(SecurityException.class, () -> authService.login(email, password));
    }

    @Test
    @DisplayName("Should return AuthResponse when credentials are correct and LDAP is disabled")
    void shouldReturnAuthResponseWhenCredentialsAreCorrectAndLdapDisabled() {
        this.authService.prodProperty("false");
        String email = "john.doe@example.com";
        String password = "password";
        User user = new User();
        user.email(email);
        Project project = new Project();
        project.id(1);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(jwtTokenUtil.generateAccessToken(user)).thenReturn("accessToken");
        when(projectRepository.findFirstByActualTrue()).thenReturn(Optional.of(project));

        AuthResponse authResponse = authService.login(email, password);

        assertNotNull(authResponse);
        assertEquals("accessToken", authResponse.accessToken());
    }

    @Test
    @DisplayName("Should throw SecurityException when credentials are incorrect and LDAP is disabled")
    void shouldThrowSecurityExceptionWhenCredentialsAreIncorrectAndLdapDisabled() {
        String email = "john.doe@example.com";
        String password = "wrongpassword";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(SecurityException.class, () -> authService.login(email, password));
    }

    @Test
    void setProdPropertyShouldSetProperty() {
        String prodProperty = "true";
        authService.prodProperty(prodProperty);
        assertEquals(prodProperty, authService.prodProperty());
    }

    @Test
    void setProdPropertyShouldSetPropertyWhenNull() {
        authService.prodProperty(null);
        assertNull(authService.prodProperty());
    }

    @Test
    void getProdPropertyShouldReturnProperty() {
        String prodProperty = "false";
        authService.prodProperty(prodProperty);
        assertEquals(prodProperty, authService.prodProperty());
    }

    @Test
    void loginShouldThrowSecurityExceptionWhenUserNotFoundAndLdapEnabled() {
        String email = "john.doe@example.com";
        String password = "password";
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(userDetails.getUsername()).thenReturn(email);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        authService.prodProperty("true");

        assertThrows(SecurityException.class, () -> authService.login(email, password));
    }

    @Test
    void loginShouldThrowSecurityExceptionWhenUserNotFoundAndLdapDisabled() {
        String email = "john.doe@example.com";
        String password = "password";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        authService.prodProperty("false");

        assertThrows(SecurityException.class, () -> authService.login(email, password));
    }

    @Test
    void loginShouldReturnAuthResponseWhenUserFoundAndLdapDisabled() {
        String email = "john.doe@example.com";
        String password = "password";
        User user = new User();
        user.email(email);
        Project project = new Project();
        project.id(1);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(jwtTokenUtil.generateAccessToken(user)).thenReturn("accessToken");
        when(projectRepository.findFirstByActualTrue()).thenReturn(Optional.of(project));
        authService.prodProperty("false");

        AuthResponse authResponse = authService.login(email, password);

        assertNotNull(authResponse);
        assertEquals(user.id(), authResponse.id());
        assertEquals("accessToken", authResponse.accessToken());
    }

    @Test
    void testLoginWithoutLDAP() {
        // Define the input values
        String email = "test@example.com";
        String password = "password";
        authService.prodProperty("false");

        // Mocking the user to be returned by the userRepository
        User mockUser = mock(User.class);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        // Mocking the JWT token generation
        String mockToken = "mockToken";
        when(jwtTokenUtil.generateAccessToken(mockUser)).thenReturn(mockToken);

        // Mocking the project repository behavior
        Project mockProject = mock(Project.class);
        when(mockProject.id()).thenReturn(1);
        when(projectRepository.findFirstByActualTrue()).thenReturn(Optional.of(mockProject));

        // Call the login method
        AuthResponse response = authService.login(email, password);

        // Verify the interactions and the expected response
        verify(userRepository).findByEmail(email);
        verify(jwtTokenUtil).generateAccessToken(mockUser);
        verify(projectRepository).findFirstByActualTrue();

        assertNotNull(response);
    }


}