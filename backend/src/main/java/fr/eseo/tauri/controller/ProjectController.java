package fr.eseo.tauri.controller;

import fr.eseo.tauri.model.Project;
import fr.eseo.tauri.service.ProjectService;
import fr.eseo.tauri.util.CustomLogger;
import fr.eseo.tauri.util.ResponseMessage;
import fr.eseo.tauri.util.valid.Create;
import fr.eseo.tauri.util.valid.Update;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
@Tag(name = "projects")
public class ProjectController {

    private final ProjectService projectService;
    private final ResponseMessage responseMessage = new ResponseMessage("project");

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable Integer id) {
        Project project = projectService.getProjectById(id);
        return ResponseEntity.ok(project);
    }

    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        List<Project> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/actual")
    public ResponseEntity<Project> getActualProject() {
        Project projects = projectService.getActualProject();
        return ResponseEntity.ok(projects);
    }

    @PostMapping("/actual/{idNewProject}")
    public ResponseEntity<String> setActualProject(@PathVariable Integer idNewProject) {
        projectService.setActualProject(idNewProject);
        CustomLogger.info(responseMessage.update());
        return ResponseEntity.ok(responseMessage.update());
    }

    @PostMapping
    public ResponseEntity<String> createProject(@Validated(Create.class) @RequestBody Project project) {
        projectService.createProject(project);
        CustomLogger.info(responseMessage.create());
        return ResponseEntity.ok(responseMessage.create());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> updateProject(@PathVariable Integer id, @Validated(Update.class) @RequestBody Project updatedProject) {
        projectService.updateProject(id, updatedProject);
        CustomLogger.info(responseMessage.update());
        return ResponseEntity.ok(responseMessage.update());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProjectById(@PathVariable Integer id) {
        projectService.deleteProjectById(id);
        CustomLogger.info(responseMessage.delete());
        return ResponseEntity.ok(responseMessage.delete());
    }

    @DeleteMapping
    public ResponseEntity<String> deleteAllProjects() {
        projectService.deleteAllProjects();
        CustomLogger.info(responseMessage.deleteAll());
        return ResponseEntity.ok(responseMessage.deleteAll());
    }
}