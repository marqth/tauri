package fr.eseo.tauri.seeder;

import fr.eseo.tauri.model.Role;
import fr.eseo.tauri.model.enumeration.RoleType;
import fr.eseo.tauri.repository.RoleRepository;
import fr.eseo.tauri.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleSeeder {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;


	@Value("${app.pl.email}")
	private String plEmail;

	public void seed() {

		// Create a project leader role for the admin user
		var rolePl = new Role();
		var userPL = userRepository.findByEmail(plEmail);
		if (userPL.isPresent()) {
			rolePl.user(userPL.get());
			rolePl.type(RoleType.PROJECT_LEADER);
			roleRepository.save(rolePl);
		}

		// Create a supervising staff role for the supervising staff user
		var roleSS = new Role();
		var userSS = userRepository.findByEmail("s.s@tauri.com");
		if (userSS.isPresent()) {
			roleSS.user(userSS.get());
			roleSS.type(RoleType.SUPERVISING_STAFF);
			roleRepository.save(roleSS);
		}

		// Create an option leader role for the option leader user
		var roleOL = new Role();
		var userOL = userRepository.findByEmail("o.l@tauri.com");
		if (userOL.isPresent()) {
			roleOL.user(userOL.get());
			roleOL.type(RoleType.OPTION_LEADER);
			roleRepository.save(roleOL);
		}

		// Create a technocal coach role for the technical coach user
		var roleTC = new Role();
		var userTC = userRepository.findByEmail("t.c@tauri.com");
		if (userTC.isPresent()) {
			roleTC.user(userTC.get());
			roleTC.type(RoleType.TECHNICAL_COACH);
			roleRepository.save(roleTC);
		}
	}

}
