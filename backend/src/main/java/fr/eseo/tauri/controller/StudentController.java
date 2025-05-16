package fr.eseo.tauri.controller;

import com.opencsv.exceptions.CsvValidationException;
import fr.eseo.tauri.model.Bonus;
import fr.eseo.tauri.model.Comment;
import fr.eseo.tauri.model.Grade;
import fr.eseo.tauri.model.Student;
import fr.eseo.tauri.service.StudentService;
import fr.eseo.tauri.util.CustomLogger;
import fr.eseo.tauri.util.ResponseMessage;
import fr.eseo.tauri.util.valid.Update;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import fr.eseo.tauri.util.valid.Create;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/students")
@Tag(name = "students")
public class StudentController {

	private final StudentService studentService;
	private final ResponseMessage responseMessage = new ResponseMessage("student");

	@GetMapping("/{id}")
	public ResponseEntity<Student> getStudentById(@PathVariable Integer id) {
		Student student = studentService.getStudentById(id);
		return ResponseEntity.ok(student);
	}

	@GetMapping
	public ResponseEntity<List<Student>> getAllStudentsByProject(@RequestParam("projectId") Integer projectId) {
		List<Student> students = studentService.getAllStudentsByProject(projectId);
		return ResponseEntity.ok(students);
	}

	@PostMapping
	public ResponseEntity<String> createStudent(@Validated(Create.class) @RequestBody Student student, @RequestParam Integer projectId ) {
		studentService.createStudent( student);
		CustomLogger.info(responseMessage.create());
		return ResponseEntity.ok(responseMessage.create());
	}

	@PatchMapping("/{id}")
	public ResponseEntity<String> updateStudent(@PathVariable Integer id, @Validated(Update.class) @RequestBody Student updatedStudent, @RequestParam("projectId") Integer projectId ) {
		studentService.updateStudent(id, updatedStudent);
		CustomLogger.info(responseMessage.update());
		return ResponseEntity.ok(responseMessage.update());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteStudent(@PathVariable Integer id) {
		studentService.deleteStudent(id);
		CustomLogger.info(responseMessage.delete());
		return ResponseEntity.ok(responseMessage.delete());
	}

	@DeleteMapping
	public ResponseEntity<String> deleteAllStudentsByProject(@RequestParam("projectId") Integer projectId) {
		studentService.deleteAllStudentsByProject(projectId);
		CustomLogger.info(responseMessage.deleteAllFromCurrentProject());
		return ResponseEntity.ok(responseMessage.deleteAllFromCurrentProject());
	}

	/**
	 * This method is responsible for handling file uploads.
	 * It is mapped to the "/uploadCSV" endpoint and only responds to HTTP POST requests.
	 *
	 * @param file This is the file that is uploaded by the client. It is expected to be a CSV file.
	 * @return ResponseEntity<String> This returns a response entity with a message indicating the result of the operation.
	 * If the file is empty, it returns a bad request response with a message "Uploaded file is empty".
	 * If the file is processed successfully, it returns an OK response with a message "File uploaded successfully".
	 * If an error occurs during the processing of the file, it returns an internal server error response with a message indicating the error.
	 */
	@PostMapping("/upload")
	public ResponseEntity<String> handleFileUpload(@RequestParam("file-upload") MultipartFile file, @RequestParam("projectId") Integer projectId) throws IOException, CsvValidationException {
		studentService.populateDatabaseFromCSV(file, projectId);
		return ResponseEntity.ok("File uploaded successfully");
	}

	@GetMapping("/download")
	public ResponseEntity<byte[]> downloadStudentsCSV(@RequestParam("projectId") Integer projectId) throws IOException {
		byte[] studentsCSV = studentService.createStudentsCSV(projectId);
		return ResponseEntity.ok(studentsCSV);
	}

	@GetMapping("/{id}/bonus")
	public ResponseEntity<Bonus> getStudentBonus(@PathVariable Integer id, @RequestParam("limited") Boolean limited, @RequestParam("sprintId") Integer sprintId) {
		Bonus bonus = studentService.getStudentBonus(id, limited, sprintId);
		return ResponseEntity.ok(bonus);
	}

	@GetMapping("/{id}/bonuses")
	public ResponseEntity<List<Bonus>> getStudentBonuses(@PathVariable Integer id, @RequestParam("sprintId") Integer sprintId) {
		List<Bonus> bonuses = studentService.getStudentBonuses(id, sprintId);
		return ResponseEntity.ok(bonuses);
	}

	@GetMapping("/{id}/sprint/{sprintId}/total")
	public ResponseEntity<Double> getIndividualTotalGrade(@PathVariable Integer id, @PathVariable Integer sprintId) {
		Double totalGrade = studentService.getIndividualTotalGrade(id, sprintId);
		return ResponseEntity.ok(totalGrade);
	}

	@GetMapping("{id}/sprint/{sprintId}/grade")
	public ResponseEntity<Double> getSprintGrade(@PathVariable Integer id, @PathVariable Integer sprintId) {
		Double sprintGrade = studentService.getSprintGrade(id, sprintId);
		return ResponseEntity.ok(sprintGrade);
	}

	@GetMapping("{id}/gradeType/{gradeTypeId}/author/{authorId}")
	public ResponseEntity<Grade> getGradeByTypeAndAuthor(@PathVariable Integer id, @PathVariable Integer gradeTypeId, @PathVariable Integer authorId, @RequestParam("sprintId") Integer sprintId) {
		Grade grade = studentService.getGradeByTypeAndAuthor(id, gradeTypeId, authorId, sprintId);
		return ResponseEntity.ok(grade);
	}

	@GetMapping("/{studentId}/sprints/{sprintId}/feedbacks")
	public ResponseEntity<List<Comment>> getFeedbacksByTeamAndSprint(@PathVariable Integer studentId, @PathVariable Integer sprintId) {
		List<Comment> comment = studentService.getFeedbacksByStudentAndSprint(studentId, sprintId);
		return ResponseEntity.ok(comment);
	}

}
