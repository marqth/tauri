package fr.eseo.tauri.controller;

import fr.eseo.tauri.model.GradeType;
import fr.eseo.tauri.service.GradeTypeService;
import fr.eseo.tauri.util.ResponseMessage;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/grade-types")
@Tag(name = "grade-types")
public class GradeTypeController {

    private final GradeTypeService gradeTypeService;
    private final ResponseMessage responseMessage = new ResponseMessage("gradetype");

    @GetMapping("/{id}")
    public ResponseEntity<GradeType> getGradeTypeById(@PathVariable Integer id) {
        GradeType gradeType = gradeTypeService.getGradeTypeById(id);
        return ResponseEntity.ok(gradeType);
    }

    @GetMapping("/imported")
    public ResponseEntity<List<GradeType>> getAllImportedGradeTypes(@RequestParam("projectId") Integer projectId) {
        List<GradeType> importedGradeTypes = gradeTypeService.getAllImportedGradeTypes(projectId);
        return ResponseEntity.ok(importedGradeTypes);
    }

    @GetMapping("/unimported")
    public ResponseEntity<List<GradeType>> getAllUnimportedGradeTypes(@RequestParam("projectId") Integer projectId) {
        List<GradeType> gradeTypes = gradeTypeService.getAllUnimportedGradeTypes(projectId);
        return ResponseEntity.ok(gradeTypes);
    }

    @PostMapping
    public ResponseEntity<String> createGradeType(@RequestBody GradeType gradeType) {
        gradeTypeService.createGradeType(gradeType);
        return ResponseEntity.ok(responseMessage.create());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> updateGradeType(@PathVariable Integer id, @RequestBody GradeType updatedGradeType,  @RequestParam("projectId") Integer projectId) {
        gradeTypeService.updateGradeType(id, updatedGradeType, projectId);
        return ResponseEntity.ok(responseMessage.update());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteGradeTypeById(@PathVariable Integer id) {
        gradeTypeService.deleteGradeTypeById(id);
        return ResponseEntity.ok(responseMessage.delete());
    }

    @DeleteMapping("/imported")
    public ResponseEntity<String> deleteAllImportedGradeTypes() {
        gradeTypeService.deleteAllImportedGradeTypes();
        return ResponseEntity.ok(responseMessage.deleteAll());
    }

    @DeleteMapping("/unimported")
    public ResponseEntity<String> deleteAllUnimportedGradeTypes() {
        gradeTypeService.deleteAllUnimportedGradeTypes();
        return ResponseEntity.ok(responseMessage.deleteAll());
    }

    @GetMapping("/name")
    public ResponseEntity<GradeType> getGradeTypeByName(@RequestParam("name") String name, @RequestParam("projectId") Integer projectId) {
        GradeType gradeType = gradeTypeService.findByName(name, projectId);
        return ResponseEntity.ok(gradeType);
    }

    @PostMapping("/{id}/upload-grade-scale")
    public ResponseEntity<String> uploadGradeScaleTXT(
            @PathVariable Integer id,
            @RequestParam("file") MultipartFile file) throws IOException {
            gradeTypeService.saveGradeScale(id, file);
            return ResponseEntity.ok(responseMessage.create());
    }

    @GetMapping("/{id}/download-grade-scale")
    public ResponseEntity<byte[]> downloadGradeScaleTXT(@PathVariable Integer id) {
        return ResponseEntity.ok(gradeTypeService.getBLOBScale(id));
    }

    @DeleteMapping("/{id}/delete-grade-scale")
    public ResponseEntity<String> deleteGradeScaleTXT(@PathVariable Integer id) {
        gradeTypeService.deleteGradeScale(id);
        return ResponseEntity.ok(responseMessage.delete());
    }

}