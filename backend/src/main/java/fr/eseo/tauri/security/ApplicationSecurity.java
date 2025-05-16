package fr.eseo.tauri.security;

import fr.eseo.tauri.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

@Configuration
@EnableWebSecurity
public class ApplicationSecurity {

    private final JwtTokenFilter jwtTokenFilter;
    private final UserRepository userRepository;

    @Autowired
    public ApplicationSecurity(JwtTokenFilter jwtTokenFilter, UserRepository userRepository) {
        this.jwtTokenFilter = jwtTokenFilter;
        this.userRepository = userRepository;
    }

    @Value("${spring.ldap.url}")
    private String ldapUrl;

    @Value("${spring.ldap.base-dn}")
    private String ldapBaseDn;

    @Value("${spring.ldap.user-search-filter}")
    private String ldapUserSearchFilter;

    @Value("${spring.ldap.username}")
    private String ldapUsername;

    @Value("${spring.ldap.password}")
    private String ldapPassword;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .headers(headers -> headers
                        .xssProtection(xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                        .contentSecurityPolicy(cps -> cps.policyDirectives("script-src 'self'")))
                .securityMatcher("/api/**", "/tauri/api/**")
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login", "/tauri/api/auth/login").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
        PasswordEncoder passwordEncoder = new LdapShaPasswordEncoder();

        auth
            .ldapAuthentication()
                .contextSource()
                    .url(ldapUrl + ldapBaseDn)
                    .managerDn(ldapUsername)
                    .managerPassword(ldapPassword)
                .and()
                .userSearchFilter(ldapUserSearchFilter)
                .passwordEncoder(passwordEncoder);

        return auth.build();
    }


    @Bean
    public UserDetailsService userDetailsService() {
        return userEmail -> userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User or password incorrect"));
    }
}