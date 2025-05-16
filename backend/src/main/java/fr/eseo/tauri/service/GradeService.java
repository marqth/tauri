package fr.eseo.tauri.service;

import com.opencsv.CSVWriter;
import fr.eseo.tauri.model.*;
import fr.eseo.tauri.model.enumeration.Gender;
import fr.eseo.tauri.model.enumeration.GradeTypeName;
import fr.eseo.tauri.model.enumeration.RoleType;
import fr.eseo.tauri.repository.*;
import fr.eseo.tauri.util.CustomLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import fr.eseo.tauri.model.Grade;
import fr.eseo.tauri.exception.ResourceNotFoundException;
import fr.eseo.tauri.repository.GradeRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;

import static fr.eseo.tauri.util.ListUtil.filter;

@Service
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository gradeRepository;
    private final UserService userService;
    @Lazy
    private final StudentService studentService;
    @Lazy
    private final SprintService sprintService;
    private final StudentRepository studentRepository;
    private final TeamRepository teamRepository;
    private final GradeTypeRepository gradeTypeRepository;
    @Lazy
    private final GradeTypeService gradeTypeService;
    private final TeamService teamService;

    public Grade getGradeById(Integer id) {
        return gradeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("grade", id));
    }

    public List<Grade> getAllUnimportedGradesByProject(Integer projectId) {
        return gradeRepository.findAllUnimportedByProject(projectId);
    }

    public List<Grade> getAllImportedGradesByProject(Integer projectId) {
        return gradeRepository.findAllImportedByProject(projectId);
    }

    public void createGrade(Grade grade) {
        setGradeAttributes(grade);
        validateGrade(grade);
        gradeRepository.save(grade);
    }

    public void checkForExistingGrade(Grade grade) {
        var ratedGrades = gradeRepository.findAllByAuthorId(grade.authorId());
        for (Grade ratedGrade : ratedGrades) {
            if (isSameGrade(grade, ratedGrade)) {
                throw new IllegalArgumentException("A grade with the same author, sprint, grade type, student and team already exists");
            }
        }
    }

    public boolean isSameGrade(Grade grade, Grade ratedGrade) {
        return ratedGrade.sprint().id().equals(grade.sprintId())
                && ratedGrade.gradeType().id().equals(grade.gradeTypeId())
                && ((ratedGrade.student() != null && ratedGrade.student().id().equals(grade.studentId())) || (ratedGrade.team() != null && ratedGrade.team().id().equals(grade.teamId())));
    }

    private void setGradeAttributes(Grade grade) {
        if (grade.authorId() != null) grade.author(userService.getUserById(grade.authorId()));
        if (grade.sprintId() != null) grade.sprint(sprintService.getSprintById(grade.sprintId()));
        if (grade.gradeTypeId() != null) grade.gradeType(gradeTypeService.getGradeTypeById(grade.gradeTypeId()));

        if (Boolean.TRUE.equals(grade.gradeType().forGroup())) {
            grade.student(null);
            if (grade.teamId() != null) grade.team(teamService.getTeamById(grade.teamId()));
        } else {
            grade.team(null);
            if (grade.studentId() != null) grade.student(studentService.getStudentById(grade.studentId()));
        }
    }

    public void validateGrade(Grade grade) {
        if ((grade.team() == null) == (grade.student() == null)) {
            throw new IllegalArgumentException("Both team and student attributes cannot be either null or not null at the same time");
        }
    }

    public void updateGrade(Integer id, Grade updatedGrade) {
        Grade grade = getGradeById(id);
        grade.value(updatedGrade.value());
        grade.comment(updatedGrade.comment());
        if (updatedGrade.sprintId() != null) grade.sprint(sprintService.getSprintById(updatedGrade.sprintId()));
        if (updatedGrade.authorId() != null) grade.author(userService.getUserById(updatedGrade.authorId()));
        if (updatedGrade.studentId() != null) grade.student(studentService.getStudentById(updatedGrade.studentId()));
        if (updatedGrade.teamId() != null) grade.team(teamService.getTeamById(updatedGrade.teamId()));

        if ((grade.team() == null) == (grade.student() == null)) {
            throw new IllegalArgumentException("Both team and student attributes cannot be either null or not null at the same time");
        }

        gradeRepository.save(grade);
    }

    public void deleteGrade(Integer id) {
        getGradeById(id);
        gradeRepository.deleteById(id);
    }

    public void deleteAllGradesByProject(Integer projectId) {
        gradeRepository.deleteAllByProject(projectId);
    }

    /**
     * This method is used to update the mean of imported grades for each student.
     */
    public void updateImportedMean(Integer projectId) {
        var students = studentRepository.findAllByProject(projectId);
        var grades = filter(gradeRepository.findAll(), grade -> grade.student() != null);
        for (var student : students) {
            if (Boolean.TRUE.equals(student.bachelor())) continue;
            var studentGrades = filter(grades, grade -> grade.student().id().equals(student.id()) && grade.gradeType().imported() && /*!grade.gradeType().name().equalsIgnoreCase("mean") && !grade.gradeType().name().equalsIgnoreCase("average")*/!grade.gradeType().name().equals(GradeTypeName.AVERAGE.displayName()));
            var mean = mean(studentGrades);
            updateImportedMeanByStudentId(mean, student.id());
        }
        CustomLogger.info("Updated imported mean for all students.");
    }

    /**
     * This method calculates the mean of a list of grades. * * @param grades the list of Grade objects for which the mean is to be calculated * @return the mean of the grades, or 0 if there are no grades or all grades have a factor of 0
     */
    public float mean(List<Grade> grades) {
        var total = 0f;
        var factors = 0f;
        for (var grade : grades) {
            total += grade.value() * grade.gradeType().factor();
            factors += grade.gradeType().factor();
        }
        if (factors == 0) return 0;
        return total / factors;
    }

    public void updateImportedMeanByStudentId(Float value, Integer studentId) {
        gradeRepository.updateImportedMeanByStudentId(value, studentId);
    }

    public Double getAverageGradesByGradeTypeByRoleType(int userId, RoleType roleType, String gradeTypeName) {
        Team team = teamRepository.findTeamByStudentId(userId);
        return gradeRepository.findAverageGradesByGradeType(team, gradeTypeName, roleType);
    }

    public Float getGradeByStudentAndGradeType(Student student, GradeType gradeType) {
        try {
            Float grade = gradeRepository.findValueByStudentAndGradeType(student, gradeType);
            CustomLogger.info("Getting grade for student " + student.name() + " and grade type " + gradeType.name() + ": " + grade);
            return grade;
        } catch (NullPointerException e) {
            CustomLogger.info("No grade found for student " + student.name() + " and grade type " + gradeType.name());
            return null;
        }
    }

    public Double getAverageByGradeTypeByStudentIdOrTeamId(Integer id, Integer sprintId, String gradeTypeName, Integer projectId) {
        GradeType gradeType = gradeTypeRepository.findByNameAndProjectId(gradeTypeName, projectId);

        Double grade;
        if (Boolean.TRUE.equals(gradeType.forGroup())) {
            grade = gradeRepository.findAverageByGradeTypeForTeam(id, sprintId, gradeTypeName);
        } else {
            grade = gradeRepository.findAverageByGradeTypeForStudent(id, sprintId, gradeTypeName);
        }
        return grade;
    }

    /**
     * This method generates a CSV report of a student's individual grades.
     *
     * @param projectId The ID of the project.
     * @return A byte array containing the CSV report.
     * @throws IOException If an I/O error occurs.
     */
    public byte[] createStudentIndividualGradesCSVReport(int projectId) throws IOException {
        CustomLogger.info("Creating student grades report for project with id " + projectId);

        CustomLogger.info("Creating student grades report for project with id " + projectId);

        // Fetch student details and grades
        List<Student> students = studentRepository.findAllByProject(projectId);
        List<GradeType> notImportedGradeTypes = gradeRepository.findAllUnimportedGradeTypesByProjectId(projectId);
        CustomLogger.info("Found " + students.size() + " students and " + notImportedGradeTypes.size() + " grade types");

        int gradeTypesCount = notImportedGradeTypes.size();
        int studentFieldsSize = 3;

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(byteArrayOutputStream);
             CSVWriter csvWriter = new CSVWriter(writer)) {

            String[] header = new String[studentFieldsSize + gradeTypesCount];
            String[] factors = new String[studentFieldsSize + gradeTypesCount];
            Arrays.fill(header, "");
            Arrays.fill(factors, "");

            for (int i = 0; i < gradeTypesCount; i++) {
                header[i + studentFieldsSize] = notImportedGradeTypes.get(i).name();
                factors[i + studentFieldsSize] = String.valueOf(notImportedGradeTypes.get(i).factor());
            }
            csvWriter.writeNext(header);
            csvWriter.writeNext(factors);

            for (Student student : students) {
                String[] studentInfo = new String[studentFieldsSize + gradeTypesCount];
                Arrays.fill(studentInfo, "");
                studentInfo[0] = student.name();
                studentInfo[1] = student.gender() == Gender.MAN ? "M" : "F";
                studentInfo[2] = Boolean.TRUE.equals(student.bachelor()) ? "B" : "";

                for (int i = 0; i < gradeTypesCount; i++) {
                    GradeType gradeType = notImportedGradeTypes.get(i);
                    Float grade = getGradeByStudentAndGradeType(student, gradeType);
                    studentInfo[i + studentFieldsSize] = grade != null ? String.valueOf(grade) : "";
                }
                csvWriter.writeNext(studentInfo);
            }

            writer.flush();

            return byteArrayOutputStream.toByteArray();
        }
    }

    public Map<String, Double> getTeamGrades(Integer teamId, Integer sprintId) {
        Map<String, Double> allGrades = new HashMap<>();

        List<GradeType> gradeTypes = gradeTypeRepository.findAllUnimportedAndForGroup();

        for (GradeType gradeType : gradeTypes) {
            Double averageGrade = gradeRepository.findAverageByGradeTypeForTeam(teamId, sprintId, gradeType.name());
            allGrades.put(gradeType.name(), averageGrade);
        }

        return allGrades;
    }

    public Map<String, Double> getTeamStudentGrades(Integer teamId, Integer sprintId) {
        Map<String, Double> allGrades = new HashMap<>();

        List<Student> teamStudents = studentRepository.findByTeam(teamId);

        for (Student student : teamStudents) {
            Double averageGrade = gradeRepository.findAverageByGradeTypeForStudent(student.id(), sprintId, "Performance individuelle");
            allGrades.put(String.valueOf(student.id()), averageGrade);
        }

        return allGrades;
    }

    public Boolean getGradesConfirmation(Integer sprintId, Integer teamId, Integer projectId) {
        try {
            Boolean gradesConfirmed = true;
            List<Student> students = studentRepository.findByTeam(teamId);
            if (students.isEmpty()) {
                return gradesConfirmed;
            }
            for (Student student : students) {
                GradeType gradeType = gradeTypeRepository.findByNameAndProjectId(GradeTypeName.INDIVIDUAL_PERFORMANCE.displayName(), projectId);
                List<Grade> grades = gradeRepository.findIsConfirmedBySprindAndStudents(sprintId, student.id(), gradeType.id());
                for (Grade g : grades){
                    if (Boolean.FALSE.equals(g.confirmed())) {
                        gradesConfirmed = false;
                    }
                }
            }
            return gradesConfirmed;
        } catch (NullPointerException e) {
            CustomLogger.info("No student or no grades found");
            return true;
        }
    }


    public Boolean setGradesConfirmation(Integer sprintId, Integer teamId, Integer projectId) {
        try {
            List<Student> students = studentRepository.findByTeam(teamId);
            if (students.isEmpty()) {
                return false;
            }

            for (Student student : students) {
                GradeType gradeType = gradeTypeRepository.findByNameAndProjectId(GradeTypeName.INDIVIDUAL_PERFORMANCE.displayName(), projectId);

                List<Grade> grades = gradeRepository.findIsConfirmedBySprindAndStudents(sprintId, student.id(), gradeType.id());
                if (grades.isEmpty()) {
                    throw new IllegalStateException("No grades found");
                }
                for (Grade grade : grades) {
                    if (Boolean.FALSE.equals(grade.confirmed())) {
                        gradeRepository.setConfirmedBySprintAndStudent(grade.id());
                    }
                }
            }

            return true;
        } catch (NullPointerException e) {
            CustomLogger.info("No student or no grades found");
            return false;
        }
    }

    public List<Grade> getRatedGradesByAuthorId(Integer authorId) {
        return gradeRepository.findAllByAuthorId(authorId);
    }

    public List<Grade> getIndividualGradesByTeam(Integer sprintId, Integer teamId){
        CustomLogger.info("Looking for individual grades for team with id " + teamId + " and sprint with id " + sprintId);
        return gradeRepository.findIndividualGradesByTeam(sprintId, teamId);
    }
}


