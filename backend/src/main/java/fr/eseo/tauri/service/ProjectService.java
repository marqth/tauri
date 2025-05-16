package fr.eseo.tauri.service;

import fr.eseo.tauri.model.GradeType;
import fr.eseo.tauri.model.Project;
import fr.eseo.tauri.exception.ResourceNotFoundException;
import fr.eseo.tauri.model.enumeration.GradeTypeName;
import fr.eseo.tauri.repository.GradeTypeRepository;
import fr.eseo.tauri.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    
    private final ProjectRepository projectRepository;
    private final GradeTypeRepository gradeTypeRepository;

    public Project getProjectById(Integer id) {
        return projectRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("project", id));
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Project getActualProject() {
        return projectRepository.findFirstByActualTrue()
                .orElseThrow(() -> new ResourceNotFoundException("actual project", 0));
    }

    public void setActualProject(Integer idNewProject) {
        Project project = projectRepository.findById(idNewProject)
                .orElseThrow(() -> new ResourceNotFoundException("project", idNewProject));
        projectRepository.findFirstByActualTrue().ifPresent(actualProject -> {
            actualProject.actual(false);
            projectRepository.save(actualProject);
        });
        project.actual(true);
        projectRepository.save(project);
    }

    public void createProject(Project project) {
        projectRepository.save(project);

        GradeTypeName[] gradeTypeNames = GradeTypeName.values();
        for(GradeTypeName gradeTypeName : gradeTypeNames) {
            if(gradeTypeName != GradeTypeName.AVERAGE) {
                var gradeType = new GradeType();

                gradeType.name(gradeTypeName.displayName());
                gradeType.factor(1.f);
                gradeType.forGroup(true);
                gradeType.imported(false);
                gradeType.project(project);

                if (gradeTypeName == GradeTypeName.INDIVIDUAL_PERFORMANCE) gradeType.forGroup(false);

                gradeTypeRepository.save(gradeType);
            }
        }
    }

    public void updateProject(Integer id, Project updatedProject) {
        Project project = getProjectById(id);

        if (updatedProject.nbTeams() != null) project.nbTeams(updatedProject.nbTeams());
        if (updatedProject.nbWomen() != null) project.nbWomen(updatedProject.nbWomen());
        if (updatedProject.phase() != null) project.phase(updatedProject.phase());

        projectRepository.save(project);
    }

    public void deleteProjectById(Integer id) {
        getProjectById(id);
        projectRepository.deleteById(id);
    }

    public void deleteAllProjects() {
        projectRepository.deleteAll();
    }
}