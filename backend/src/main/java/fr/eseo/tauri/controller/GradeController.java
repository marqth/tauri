package fr.eseo.tauri.controller;

import fr.eseo.tauri.model.Grade;
import fr.eseo.tauri.model.GradeType;
import fr.eseo.tauri.model.enumeration.RoleType;
import fr.eseo.tauri.repository.GradeTypeRepository;
import fr.eseo.tauri.service.GradeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import fr.eseo.tauri.util.CustomLogger;
import fr.eseo.tauri.util.ResponseMessage;
import fr.eseo.tauri.util.valid.Create;
import fr.eseo.tauri.util.valid.Update;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/grades")
@Tag(name = "grades")
public class GradeController {

    private final GradeService gradeService;
    private final ResponseMessage responseMessage = new ResponseMessage("grade");
    private final GradeTypeRepository gradeTypeRepository;

    @GetMapping("/{id}")
    public ResponseEntity<Grade> getGradeById(@PathVariable Integer id) {
        Grade grade = gradeService.getGradeById(id);
        return ResponseEntity.ok(grade);
    }

    @GetMapping("/unimported")
    public ResponseEntity<List<Grade>> getAllUnimportedGradesByProject(@RequestParam("projectId") Integer projectId) {
        List<Grade> grades = gradeService.getAllUnimportedGradesByProject(projectId);
        return ResponseEntity.ok(grades);
    }

    @GetMapping("/imported")
    public ResponseEntity<List<Grade>> getAllImportedGradesByProject(@RequestParam("projectId") Integer projectId) {
        List<Grade> importedGrades = gradeService.getAllImportedGradesByProject(projectId);
        return ResponseEntity.ok(importedGrades);
    }

    @PostMapping
    public ResponseEntity<String> createGrade(@Validated(Create.class) @RequestBody Grade grade) {
        gradeService.createGrade(grade);
        CustomLogger.info(responseMessage.create());
        return ResponseEntity.ok(responseMessage.create());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> updateGrade(@PathVariable Integer id, @Validated(Update.class) @RequestBody Grade updatedGrade) {
        gradeService.updateGrade(id, updatedGrade);
        CustomLogger.info(responseMessage.update());
        return ResponseEntity.ok(responseMessage.update());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteGrade(@PathVariable Integer id) {
        gradeService.deleteGrade(id);
        CustomLogger.info(responseMessage.delete());
        return ResponseEntity.ok(responseMessage.delete());
    }

    @DeleteMapping
    public ResponseEntity<String> deleteAllGradesByProject(@RequestParam("projectId") Integer projectId) {
        gradeService.deleteAllGradesByProject(projectId);
        CustomLogger.info(responseMessage.deleteAllFromCurrentProject());
        return ResponseEntity.ok(responseMessage.deleteAllFromCurrentProject());
    }

    //@GetMapping("/unimported/averages") => On peut récup le user dans le token ?
    @GetMapping("/average-grades-by-grade-type-by-role/{userId}")
    public ResponseEntity<List<List<Double>>> getAverageGradesByGradeTypeByRole(@PathVariable Integer userId) {
        ArrayList<List<Double>> gradeByTypes = new ArrayList<>();
        ArrayList<Double> gradeByRoles;

        for (GradeType gradeType : gradeTypeRepository.findAllForGroup()) {
            gradeByRoles = new ArrayList<>();
            for (RoleType roleType : RoleType.values()) {
                double grade = getAverageGradeByRoleType(userId, roleType, gradeType.name());
                gradeByRoles.add(grade);
            }
            gradeByTypes.add(gradeByRoles);
        }

        if (gradeByTypes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.status(HttpStatus.OK).body(gradeByTypes);
    }

    /**
     * Helper method
     */
    private double getAverageGradeByRoleType(Integer userId, RoleType roleType, String gradeType) {
        try {
            return gradeService.getAverageGradesByGradeTypeByRoleType(userId, roleType, gradeType);
        } catch (NullPointerException e) {
            return -1.0;
        }
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadStudentGrades(@RequestParam("projectId") Integer projectId) throws IOException {
        byte[] gradesCSV = gradeService.createStudentIndividualGradesCSVReport(projectId);
        return ResponseEntity.ok(gradesCSV);
    }

    @GetMapping("/average/{id}")
    public double getAverageGradeTypeByStudentIdOrTeamId(@PathVariable Integer id,@RequestParam("sprintId") Integer sprintId,@RequestParam("gradeTypeName") String gradeTypeName, @RequestParam("projectId") Integer projectId) {
        try{
            return gradeService.getAverageByGradeTypeByStudentIdOrTeamId(id, sprintId,gradeTypeName, projectId);
        } catch (NullPointerException e){
            return -1.0;
        }
    }

    @GetMapping("/average-team/{teamId}")
    public ResponseEntity<Map<String, Double>> getTeamGrades(@PathVariable Integer teamId, @RequestParam("sprintId") Integer sprintId) {
        Map<String, Double> teamGrades = gradeService.getTeamGrades(teamId, sprintId);
        return ResponseEntity.ok(teamGrades);
    }

    @GetMapping("/average-students/{teamId}")
    public ResponseEntity<Map<String, Double>> getTeamStudentGrades(@PathVariable Integer teamId, @RequestParam("sprintId") Integer sprintId) {
        Map<String, Double> teamStudentGrades = gradeService.getTeamStudentGrades(teamId, sprintId);
        return ResponseEntity.ok(teamStudentGrades);
    }

    @GetMapping("/confirmation/{sprintId}/team/{teamId}")
    public ResponseEntity<Boolean> getGradesConfirmations(@PathVariable Integer sprintId, @PathVariable Integer teamId, @RequestParam("projectId") Integer projectId) {
        return ResponseEntity.ok(gradeService.getGradesConfirmation(sprintId, teamId, projectId));
    }

    @PostMapping("/confirmation/{sprintId}/team/{teamId}")
    public ResponseEntity<Boolean> setGradesConfirmations(@PathVariable Integer sprintId, @PathVariable Integer teamId, @RequestParam("projectId") Integer projectId) {
        return ResponseEntity.ok(gradeService.setGradesConfirmation(sprintId, teamId, projectId));
    }

    @GetMapping("/individual-grades-by-team/{sprintId}/{teamId}")
    public List<Grade> getInduvidualGradesByTeam(@PathVariable Integer sprintId, @PathVariable Integer teamId) {
        return ResponseEntity.ok(gradeService.getIndividualGradesByTeam(sprintId, teamId)).getBody();
    }
}
