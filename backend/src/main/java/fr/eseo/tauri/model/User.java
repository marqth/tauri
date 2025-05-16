package fr.eseo.tauri.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.eseo.tauri.util.valid.Create;
import fr.eseo.tauri.util.valid.Update;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
public class User implements UserDetails {

	public User(String name, String email, String password, String privateKey) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.privateKey = privateKey;
	}

	public User(String email) {
		this.email = email;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonProperty
	private Integer id;

	@NotNull(groups = { Create.class }, message = "The name field is required")
	@JsonProperty
	private String name;

	@NotNull(groups = { Create.class }, message = "The email field is required")
	@Email(groups = { Create.class, Update.class }, message = "The email field must be a valid email")
	@Column(unique = true)
	@JsonProperty
	private String email;

	@NotNull(groups = { Create.class }, message = "The password field is required")
	@JsonProperty
	private String password;

	@NotNull(groups = { Create.class }, message = "The privateKey field is required")
	@JsonProperty
	private String privateKey;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.emptyList();
	}

	@Override
	public String getPassword() {
		return this.password();
	}

	@Override
	public String getUsername() {
		return this.email();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}