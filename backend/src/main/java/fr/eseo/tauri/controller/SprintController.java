package fr.eseo.tauri.controller;

import fr.eseo.tauri.model.Comment;
import fr.eseo.tauri.model.Sprint;
import fr.eseo.tauri.service.SprintService;
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
@RequestMapping("/api/sprints")
@Tag(name = "sprints")
public class SprintController {

    private final SprintService sprintService;
    private final ResponseMessage responseMessage = new ResponseMessage("sprint");

    @GetMapping("/{id}")
    public ResponseEntity<Sprint> getSprintById(@PathVariable Integer id) {
        Sprint sprint = sprintService.getSprintById(id);
        return ResponseEntity.ok(sprint);
    }

    @GetMapping
    public ResponseEntity<List<Sprint>> getAllSprintsByProject(@RequestParam("projectId") Integer projectId) {
        List<Sprint> sprints = sprintService.getAllSprintsByProject(projectId);
        return ResponseEntity.ok(sprints);
    }

    @PostMapping
    public ResponseEntity<String> createSprint(@Validated(Create.class) @RequestBody Sprint sprint, @RequestParam Integer projectId) {
        sprintService.createSprint(sprint, projectId);
        CustomLogger.info(responseMessage.create());
        return ResponseEntity.ok(responseMessage.create());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> updateSprint(@PathVariable Integer id, @Validated(Update.class) @RequestBody Sprint updatedSprint) {
        sprintService.updateSprint(id, updatedSprint);
        CustomLogger.info(responseMessage.update());
        return ResponseEntity.ok(responseMessage.update());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSprint(@PathVariable Integer id) {
        sprintService.deleteSprint(id);
        CustomLogger.info(responseMessage.delete());
        return ResponseEntity.ok(responseMessage.delete());
    }

    @DeleteMapping
    public ResponseEntity<String> deleteAllSprintsByProject(@RequestParam("projectId") Integer projectId) {
        sprintService.deleteAllSprintsByProject(projectId);
        CustomLogger.info(responseMessage.deleteAllFromCurrentProject());
        return ResponseEntity.ok(responseMessage.deleteAllFromCurrentProject());
    }

    @GetMapping("/current")
    public ResponseEntity<Sprint> getCurrentSprint(@RequestParam("projectId") Integer projectId) {
        Sprint sprint = sprintService.getCurrentSprint(projectId);
        return ResponseEntity.ok(sprint);
    }

    @GetMapping("/{sprintId}/author/{authorId}/student-comments")
    public ResponseEntity<List<Comment>> getTeamStudentsComments(@PathVariable Integer sprintId, @PathVariable Integer authorId, @RequestParam("teamId") Integer teamId){
        List<Comment> teamStudentComments = sprintService.getTeamStudentsComments(sprintId, authorId, teamId);
        return ResponseEntity.ok(teamStudentComments);
    }
}