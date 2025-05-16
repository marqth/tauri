package fr.eseo.tauri.seeder;

import fr.eseo.tauri.model.User;
import fr.eseo.tauri.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSeeder {


	private final UserRepository userRepository;


	@Value("${app.pl.email}")
	private String plEmail;
	@Value("${app.pl.name}")
	private String plName;

	public void seed() {

		var userPL = new User(plEmail);
		userPL.name(plName);
		userRepository.save(userPL);

		var userSS = new User("s.s@tauri.com");
		userSS.name("CLAVREUL Michael");
		userRepository.save(userSS);

		var userOL = new User("o.l@tauri.com");
		userOL.name("ROUSSEAU Sophie");
		userRepository.save(userOL);

		var userTC = new User("t.c@tauri.com");
		userTC.name("Technique Coach");
		userRepository.save(userTC);

	}
}
