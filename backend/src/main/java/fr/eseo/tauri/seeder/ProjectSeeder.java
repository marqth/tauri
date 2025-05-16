package fr.eseo.tauri.seeder;

import fr.eseo.tauri.model.Project;
import fr.eseo.tauri.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectSeeder {

	private final ProjectRepository projectRepository;

	public void seed() {
		if (projectRepository.count() == 0) {
			// Add a default project if the table is empty
			Project project = new Project();
			project.name("Premier projet");
			project.actual(true);
			projectRepository.save(project);
		}
	}
}
