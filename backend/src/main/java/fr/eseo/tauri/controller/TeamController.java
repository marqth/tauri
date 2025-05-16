package fr.eseo.tauri.controller;

import fr.eseo.tauri.model.*;
import fr.eseo.tauri.service.PresentationOrderService;
import fr.eseo.tauri.service.TeamService;
import fr.eseo.tauri.util.CustomLogger;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import fr.eseo.tauri.util.ResponseMessage;
import fr.eseo.tauri.util.valid.Create;
import fr.eseo.tauri.util.valid.Update;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Controller class for managing teams.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teams")
@Tag(name = "teams")
public class TeamController {

    private final TeamService teamService;
    private final PresentationOrderService presentationOrderService;
    private final ResponseMessage responseMessage = new ResponseMessage("team");

    @GetMapping("/{id}")
    public ResponseEntity<Team> getTeamById(@PathVariable Integer id) {
        Team team = teamService.getTeamById(id);
        return ResponseEntity.ok(team);
    }

    @GetMapping
    public ResponseEntity<List<Team>> getAllTeamsByProject(@RequestParam("projectId") Integer projectId) {
        List<Team> teams = teamService.getAllTeamsByProject(projectId);
        return ResponseEntity.ok(teams);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> updateTeam(@PathVariable Integer id, @Validated(Update.class) @RequestBody Team updatedTeam) {
        teamService.updateTeam(id, updatedTeam);
        CustomLogger.info(responseMessage.update());
        return ResponseEntity.ok(responseMessage.update());
    }

    @DeleteMapping
    public ResponseEntity<String> deleteAllTeamsByProject(@RequestParam("projectId") Integer projectId) {
        teamService.deleteAllTeamsByProject(projectId);
        CustomLogger.info(responseMessage.deleteAllFromCurrentProject());
        return ResponseEntity.ok(responseMessage.deleteAllFromCurrentProject());
    }

    @GetMapping("/{id}/students")
    public ResponseEntity<List<Student>> getStudentsByTeamId(@PathVariable Integer id, @RequestParam("ordered") Boolean ordered) {
        var students = teamService.getStudentsByTeamId(id, ordered);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/{id}/criteria")
    public ResponseEntity<Criteria> getCriteriaByTeamId(@PathVariable Integer id, @RequestParam("projectId") Integer projectId) {
        Criteria criteria = teamService.getCriteriaByTeamId(id, projectId);
        return ResponseEntity.ok(criteria);
    }

    @GetMapping("/{id}/average")
    public ResponseEntity<Double> getTeamAvgGrade(@PathVariable Integer id) {
        var avgGrade = this.teamService.getTeamAvgGrade(id);
        return ResponseEntity.ok(avgGrade);
    }

    /**
     * Create teams.
     *
     * @return a response entity with a success message if the update was successful, otherwise an error message
     */
    @PostMapping
    public ResponseEntity<String> generateTeams(@RequestParam("projectId") Integer projectId, @RequestParam("autoWomenRatio") Boolean autoWomenRatio, @Validated(Create.class) @RequestBody Project projectDetails) {
        teamService.generateTeams(projectId, projectDetails, autoWomenRatio);
        CustomLogger.info(responseMessage.create());
        return ResponseEntity.ok(responseMessage.create());
    }

    @GetMapping("/{teamId}/sprints/{sprintId}/feedbacks")
    public ResponseEntity<List<Comment>> getFeedbacksByTeamAndSprint(@PathVariable Integer teamId, @PathVariable Integer sprintId) {
        List<Comment> comment = teamService.getFeedbacksByTeamAndSprint(teamId, sprintId);
        return ResponseEntity.ok(comment);
    }

    @GetMapping("/{id}/sprint/{sprintId}/total")
    public ResponseEntity<Double> getTeamTotalGrade(@PathVariable Integer id, @PathVariable Integer sprintId) {
        Double totalGrade = teamService.getTeamTotalGrade(id, sprintId);
        return ResponseEntity.ok(totalGrade);
    }

    @GetMapping("/{id}/sprint/{sprintId}/individual/totals")
    public ResponseEntity<List<Double>> getIndividualTotalGrades(@PathVariable Integer id, @PathVariable Integer sprintId) {
        List<Double> studentsTotalGrades = teamService.getIndividualTotalGrades(id, sprintId);
        return ResponseEntity.ok(studentsTotalGrades);
    }

    @GetMapping("{id}/sprint/{sprintId}/grades")
    public ResponseEntity<List<Double>> getSprintGrades(@PathVariable Integer id, @PathVariable Integer sprintId) {
        List<Double> sprintGrade = teamService.getSprintGrades(id, sprintId);
        return ResponseEntity.ok(sprintGrade);
    }

    @GetMapping("/sprint/{sprintId}/average")
    public ResponseEntity<List<Double>> getAverageSprintGrades(@PathVariable Integer sprintId) {
        List<Double> sprintGrade = teamService.getAverageSprintGrades(sprintId);
        return ResponseEntity.ok(sprintGrade);
    }

    @GetMapping("/{id}/presentation-order")
    public ResponseEntity<List<PresentationOrder>> getPresentationOrderByTeamIdAndSprintId(@PathVariable Integer id, @RequestParam Integer sprintId) {
        List<PresentationOrder> presentationOrder = presentationOrderService.getPresentationOrderByTeamIdAndSprintId(id, sprintId);
        return ResponseEntity.ok(presentationOrder);
    }

    @PatchMapping("/{id}/presentation-order")
    public ResponseEntity<String> updatePresentationOrderByTeamIdAndSprintId(@PathVariable Integer id, @RequestParam Integer sprintId, @RequestBody List<Student> students) {
        presentationOrderService.updatePresentationOrderByTeamIdAndSprintId(id, sprintId, students);
        return ResponseEntity.ok(responseMessage.update());
    }


    @GetMapping("/{teamId}/sprints/{sprintId}/individual-comments")
    public ResponseEntity<List<Comment>> getIndividualCommentsByTeamIdAndSprintId(@PathVariable Integer teamId, @PathVariable Integer sprintId) {
        List<Comment> comments = teamService.getIndividualCommentsByTeamIdAndSprintId(teamId, sprintId);
        return ResponseEntity.ok(comments);
    }
}
