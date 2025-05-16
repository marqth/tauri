package fr.eseo.tauri.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import fr.eseo.tauri.model.Project;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Integer> {
    Optional<Project> findFirstByActualTrue();

}
