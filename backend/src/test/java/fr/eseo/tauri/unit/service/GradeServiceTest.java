package fr.eseo.tauri.unit.service;

import fr.eseo.tauri.exception.ResourceNotFoundException;
import fr.eseo.tauri.model.*;
import fr.eseo.tauri.model.enumeration.Gender;
import fr.eseo.tauri.model.enumeration.GradeTypeName;
import fr.eseo.tauri.model.enumeration.RoleType;
import fr.eseo.tauri.repository.GradeRepository;
import fr.eseo.tauri.repository.GradeTypeRepository;
import fr.eseo.tauri.repository.StudentRepository;
import fr.eseo.tauri.repository.TeamRepository;
import fr.eseo.tauri.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Nested
class GradeServiceTest {

    @Mock
    private GradeRepository gradeRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private UserService userService;

    @Mock
    private SprintService sprintService;

    @Mock
    private GradeTypeService gradeTypeService;

    @Mock
    private TeamService teamService;

    @Mock
    private StudentService studentService;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private GradeTypeRepository gradeTypeRepository;

    @InjectMocks
    private GradeService gradeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getGradeByIdShouldReturnGradeWhenAuthorized() {
        Grade grade = new Grade();
        when(gradeRepository.findById(anyInt())).thenReturn(Optional.of(grade));

        Grade result = gradeService.getGradeById(1);

        assertEquals(grade, result);
    }

    @Test
    void getGradeByIdShouldThrowResourceNotFoundExceptionWhenGradeNotFound() {
        when(gradeRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> gradeService.getGradeById(1));
    }

    @Test
    void getAllUnimportedGradesByProjectShouldReturnGradesWhenAuthorized() {
        List<Grade> grades = Collections.singletonList(new Grade());

        when(gradeRepository.findAllUnimportedByProject(anyInt())).thenReturn(grades);

        List<Grade> result = gradeService.getAllUnimportedGradesByProject(1);

        assertEquals(grades, result);
    }

    @Test
    void getAllImportedGradesByProjectShouldReturnGradesWhenAuthorized() {
        List<Grade> grades = Collections.singletonList(new Grade());
        when(gradeRepository.findAllImportedByProject(anyInt())).thenReturn(grades);

        List<Grade> result = gradeService.getAllImportedGradesByProject(1);

        assertEquals(grades, result);
    }

    @Test
    void createGradeShouldSaveGradeWhenAuthorizedAndGradeTypeForGroup() {
        Grade grade = new Grade();
        grade.authorId(1);
        grade.sprintId(1);
        grade.gradeTypeId(1);
        grade.teamId(1);
        GradeType gradeType = new GradeType();
        gradeType.forGroup(true);
        grade.gradeType(gradeType);

        when(userService.getUserById(anyInt())).thenReturn(new User());
        when(sprintService.getSprintById(anyInt())).thenReturn(new Sprint());
        when(gradeTypeService.getGradeTypeById(anyInt())).thenReturn(gradeType);
        when(teamService.getTeamById(anyInt())).thenReturn(new Team());

        gradeService.createGrade(grade);

        verify(gradeRepository, times(1)).save(any(Grade.class));
    }

    @Test
    void createGradeShouldSaveGradeWhenAuthorizedAndGradeTypeNotForGroup() {
        Grade grade = new Grade();
        grade.authorId(1);
        grade.sprintId(1);
        grade.gradeTypeId(1);
        grade.studentId(1);
        GradeType gradeType = new GradeType();
        gradeType.forGroup(false);
        grade.gradeType(gradeType);

        when(userService.getUserById(anyInt())).thenReturn(new User());
        when(sprintService.getSprintById(anyInt())).thenReturn(new Sprint());
        when(gradeTypeService.getGradeTypeById(anyInt())).thenReturn(gradeType);
        when(studentService.getStudentById(anyInt())).thenReturn(new Student());

        gradeService.createGrade(grade);

        verify(gradeRepository, times(1)).save(any(Grade.class));
    }

    @Test
    void deleteGradeShouldDeleteGradeWhenAuthorized() {
        when(gradeRepository.findById(anyInt())).thenReturn(Optional.of(new Grade()));

        gradeService.deleteGrade(1);

        verify(gradeRepository, times(1)).deleteById(anyInt());
    }

    @Test
    void deleteGradeShouldThrowResourceNotFoundExceptionWhenGradeNotFound() {
        when(gradeRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> gradeService.deleteGrade(1));
    }

    @Test
    void deleteAllGradesByProjectShouldDeleteGradesWhenAuthorized() {
        gradeService.deleteAllGradesByProject(1);

        verify(gradeRepository, times(1)).deleteAllByProject(anyInt());
    }

    @Test
    void meanShouldReturnZeroWhenNoGrades() {
        List<Grade> grades = Collections.emptyList();

        float result = gradeService.mean(grades);

        assertEquals(0, result);
    }

    @Test
    void meanShouldReturnZeroWhenAllFactorsAreZero() {
        GradeType gradeType = new GradeType();
        gradeType.factor(0f);
        Grade grade1 = new Grade();
        grade1.value(90f);
        grade1.gradeType(gradeType);
        Grade grade2 = new Grade();
        grade2.value(80f);
        grade2.gradeType(gradeType);
        List<Grade> grades = Arrays.asList(grade1, grade2);

        float result = gradeService.mean(grades);

        assertEquals(0, result);
    }

    @Test
    void meanShouldReturnCorrectMeanWhenGradesWithFactors() {
        GradeType gradeType1 = new GradeType();
        gradeType1.factor(2f);
        Grade grade1 = new Grade();
        grade1.value(90f);
        grade1.gradeType(gradeType1);
        GradeType gradeType2 = new GradeType();
        gradeType2.factor(1f);
        Grade grade2 = new Grade();
        grade2.value(80f);
        grade2.gradeType(gradeType2);
        List<Grade> grades = Arrays.asList(grade1, grade2);

        float result = gradeService.mean(grades);

        assertEquals(86.67f, result, 0.01f);
    }

    @Test
    void createStudentIndividualGradesCSVReportShouldGenerateCorrectReportWhenAuthorized() throws IOException {
        int projectId = 1;
        Student student = new Student();
        student.name("John Doe");
        student.gender(Gender.MAN);
        student.bachelor(true);
        GradeType gradeType = new GradeType();
        gradeType.name("Test Grade");
        gradeType.factor(1f);
        List<Student> students = Collections.singletonList(student);
        List<GradeType> gradeTypes = Collections.singletonList(gradeType);

        when(studentRepository.findAllByProject(projectId)).thenReturn(students);
        when(gradeRepository.findAllUnimportedGradeTypesByProjectId(projectId)).thenReturn(gradeTypes);
        when(gradeService.getGradeByStudentAndGradeType(student, gradeType)).thenReturn(90f);

        byte[] result = gradeService.createStudentIndividualGradesCSVReport(projectId);

        String expectedCsv = """
                "","","","Test Grade"
                "","","","1.0"
                "John Doe","M","B","90.0"
                """;
        String actualCsv = new String(result);

        assertEquals(expectedCsv, actualCsv);
    }

    @Test
    void createStudentIndividualGradesCSVReportShouldHandleNoGrades() throws IOException {
        int projectId = 1;
        Student student = new Student();
        student.name("John Doe");
        student.gender(Gender.MAN);
        student.bachelor(true);
        List<Student> students = Collections.singletonList(student);
        List<GradeType> gradeTypes = Collections.emptyList();

        when(studentRepository.findAllByProject(projectId)).thenReturn(students);
        when(gradeRepository.findAllUnimportedGradeTypesByProjectId(projectId)).thenReturn(gradeTypes);

        byte[] result = gradeService.createStudentIndividualGradesCSVReport(projectId);

        String expectedCsv = """
                "","",""
                "","",""
                "John Doe","M","B"
                """;
        String actualCsv = new String(result);

        assertEquals(expectedCsv, actualCsv);
    }


    @Test
    void updateImportedMeanShouldNotUpdateMeanForBachelorStudents() {
        Student student = new Student();
        student.id(1);
        student.bachelor(true);
        List<Student> students = Collections.singletonList(student);

        when(studentRepository.findAll()).thenReturn(students);

        gradeService.updateImportedMean(1);

        verify(gradeRepository, never()).updateImportedMeanByStudentId(anyFloat(), anyInt());
    }

    @Test
    void updateImportedMeanByStudentIdShouldUpdateWhenValueIsNotNullAndStudentIdIsValid() {
        Float value = 85.0f;
        Integer studentId = 1;

        gradeService.updateImportedMeanByStudentId(value, studentId);

        verify(gradeRepository, times(1)).updateImportedMeanByStudentId(value, studentId);
    }

    @Test
    void updateImportedMeanByStudentIdShouldNotUpdateWhenValueIsNull() {

        Integer studentId = 1;

        gradeService.updateImportedMeanByStudentId(null, studentId);

        verify(gradeRepository, never()).updateImportedMeanByStudentId(anyFloat(), anyInt());
    }

    @Test
    void updateImportedMeanByStudentIdShouldNotUpdateWhenStudentIdIsNull() {
        Float value = 85.0f;

        gradeService.updateImportedMeanByStudentId(value, null);

        verify(gradeRepository, never()).updateImportedMeanByStudentId(anyFloat(), anyInt());
    }

    @Test
    void getAverageGradesByGradeTypeByRoleTypeShouldReturnAverageWhenTeamExists() {
        int userId = 1;
        RoleType roleType = RoleType.TEAM_MEMBER;
        String gradeTypeName = "Test Grade";
        Team team = new Team();
        Double expectedAverage = 85.0;

        when(teamRepository.findTeamByStudentId(userId)).thenReturn(team);
        when(gradeRepository.findAverageGradesByGradeType(team, gradeTypeName, roleType)).thenReturn(expectedAverage);

        Double actualAverage = gradeService.getAverageGradesByGradeTypeByRoleType(userId, roleType, gradeTypeName);

        assertEquals(expectedAverage, actualAverage);
    }

    @Test
    void getAverageGradesByGradeTypeByRoleTypeShouldReturnZeroWhenNoTeamExists() {
        int userId = 1;
        RoleType roleType = RoleType.TEAM_MEMBER;
        String gradeTypeName = "Test Grade";

        when(teamRepository.findTeamByStudentId(userId)).thenReturn(null);

        Double actualAverage = gradeService.getAverageGradesByGradeTypeByRoleType(userId, roleType, gradeTypeName);

        assertEquals(0.0, actualAverage);
    }

    @Test
    void getAverageGradesByGradeTypeByRoleTypeShouldReturnNullWhenNoAverageGradeExists() {
        int userId = 1;
        RoleType roleType = RoleType.TEAM_MEMBER;
        String gradeTypeName = "Test Grade";
        Team team = new Team();

        when(teamRepository.findTeamByStudentId(userId)).thenReturn(team);
        when(gradeRepository.findAverageGradesByGradeType(team, gradeTypeName, roleType)).thenReturn(null);

        Double actualAverage = gradeService.getAverageGradesByGradeTypeByRoleType(userId, roleType, gradeTypeName);

        assertNull(actualAverage);
    }

    @Test
    void getAverageByGradeTypeByStudentIdOrTeamIdShouldReturnTeamAverageWhenGradeTypeForGroup() {
        Integer id = 1;
        Integer sprintId = 1;
        String gradeTypeName = "Test Grade";
        GradeType gradeType = new GradeType();
        gradeType.forGroup(true);
        Double expectedAverage = 85.0;
        Integer projectId = 1;


        when(gradeTypeRepository.findByNameAndProjectId(gradeTypeName, projectId)).thenReturn(gradeType);
        when(gradeRepository.findAverageByGradeTypeForTeam(id, sprintId, gradeTypeName)).thenReturn(expectedAverage);

        Double actualAverage = gradeService.getAverageByGradeTypeByStudentIdOrTeamId(id, sprintId, gradeTypeName, projectId);

        assertEquals(expectedAverage, actualAverage);
    }

    @Test
    void getAverageByGradeTypeByStudentIdOrTeamIdShouldReturnStudentAverageWhenGradeTypeNotForGroup() {
        Integer id = 1;
        Integer sprintId = 1;
        String gradeTypeName = "Test Grade";
        GradeType gradeType = new GradeType();
        gradeType.forGroup(false);
        Double expectedAverage = 90.0;
        Integer projectId = 1;


        when(gradeTypeRepository.findByNameAndProjectId(gradeTypeName, projectId)).thenReturn(gradeType);
        when(gradeRepository.findAverageByGradeTypeForStudent(id, sprintId, gradeTypeName)).thenReturn(expectedAverage);

        Double actualAverage = gradeService.getAverageByGradeTypeByStudentIdOrTeamId(id, sprintId, gradeTypeName, projectId);

        assertEquals(expectedAverage, actualAverage);
    }

    @Test
    void getAverageByGradeTypeByStudentIdOrTeamIdShouldReturnNullWhenNoAverageExists() {
        Integer id = 1;
        Integer sprintId = 1;
        String gradeTypeName = "Test Grade";
        GradeType gradeType = new GradeType();
        gradeType.forGroup(true);
        Integer projectId = 1;


        when(gradeTypeRepository.findByNameAndProjectId(gradeTypeName, projectId)).thenReturn(gradeType);
        when(gradeRepository.findAverageByGradeTypeForTeam(id, sprintId, gradeTypeName)).thenReturn(null);

        Double actualAverage = gradeService.getAverageByGradeTypeByStudentIdOrTeamId(id, sprintId, gradeTypeName, projectId);

        assertNull(actualAverage);
    }

    @Test
    void getTeamGradesShouldReturnCorrectGradesWhenGradeTypesExist() {
        Integer teamId = 1;
        Integer sprintId = 1;
        GradeType gradeType = new GradeType();
        gradeType.name("Test Grade");
        List<GradeType> gradeTypes = Collections.singletonList(gradeType);
        Double expectedAverage = 85.0;

        when(gradeTypeRepository.findAllUnimportedAndForGroup()).thenReturn(gradeTypes);
        when(gradeRepository.findAverageByGradeTypeForTeam(teamId, sprintId, gradeType.name())).thenReturn(expectedAverage);

        Map<String, Double> actualGrades = gradeService.getTeamGrades(teamId, sprintId);

        assertEquals(expectedAverage, actualGrades.get(gradeType.name()));
    }

    @Test
    void getTeamGradesShouldReturnEmptyMapWhenNoGradeTypesExist() {
        Integer teamId = 1;
        Integer sprintId = 1;

        when(gradeTypeRepository.findAllUnimportedAndForGroup()).thenReturn(Collections.emptyList());

        Map<String, Double> actualGrades = gradeService.getTeamGrades(teamId, sprintId);

        assertTrue(actualGrades.isEmpty());
    }

    @Test
    void getTeamGradesShouldReturnNullValueInMapWhenNoAverageGradeExists() {
        Integer teamId = 1;
        Integer sprintId = 1;
        GradeType gradeType = new GradeType();
        gradeType.name("Test Grade");
        List<GradeType> gradeTypes = Collections.singletonList(gradeType);

        when(gradeTypeRepository.findAllUnimportedAndForGroup()).thenReturn(gradeTypes);
        when(gradeRepository.findAverageByGradeTypeForTeam(teamId, sprintId, gradeType.name())).thenReturn(null);

        Map<String, Double> actualGrades = gradeService.getTeamGrades(teamId, sprintId);

        assertNull(actualGrades.get(gradeType.name()));
    }

    @Test
    void getTeamStudentGradesShouldReturnCorrectGradesWhenStudentsExist() {
        Integer teamId = 1;
        Integer sprintId = 1;
        Student student = new Student();
        student.id(1);
        List<Student> teamStudents = Collections.singletonList(student);
        Double expectedAverage = 85.0;

        when(studentRepository.findByTeam(teamId)).thenReturn(teamStudents);
        when(gradeRepository.findAverageByGradeTypeForStudent(student.id(), sprintId, "Performance individuelle")).thenReturn(expectedAverage);

        Map<String, Double> actualGrades = gradeService.getTeamStudentGrades(teamId, sprintId);

        assertEquals(expectedAverage, actualGrades.get(String.valueOf(student.id())));
    }

    @Test
    void getTeamStudentGradesShouldReturnEmptyMapWhenNoStudentsExist() {
        Integer teamId = 1;
        Integer sprintId = 1;

        when(studentRepository.findByTeam(teamId)).thenReturn(Collections.emptyList());

        Map<String, Double> actualGrades = gradeService.getTeamStudentGrades(teamId, sprintId);

        assertTrue(actualGrades.isEmpty());
    }

    @Test
    void getTeamStudentGradesShouldReturnNullValueInMapWhenNoAverageGradeExists() {
        Integer teamId = 1;
        Integer sprintId = 1;
        Student student = new Student();
        student.id(1);
        List<Student> teamStudents = Collections.singletonList(student);

        when(studentRepository.findByTeam(teamId)).thenReturn(teamStudents);
        when(gradeRepository.findAverageByGradeTypeForStudent(student.id(), sprintId, "Performance individuelle")).thenReturn(null);

        Map<String, Double> actualGrades = gradeService.getTeamStudentGrades(teamId, sprintId);

        assertNull(actualGrades.get(String.valueOf(student.id())));
    }


    @Test
    void setGradesConfirmationShouldReturnFalseWhenNoStudentsInTeam() {
        Integer teamId = 1;
        Integer sprintId = 1;
        Integer projectId = 1;

        when(studentRepository.findByTeam(teamId)).thenReturn(Collections.emptyList());

        Boolean result = gradeService.setGradesConfirmation(teamId, sprintId, projectId);

        assertFalse(result);
    }


    @Test
    void setGradesConfirmationShouldReturnFalseWhenNoGradesFound() {
        Integer teamId = 1;
        Integer sprintId = 1;
        Integer projectId = 1;
        Student student = new Student();
        student.id(1);
        List<Student> students = Collections.singletonList(student);
        GradeType gradeType = new GradeType();
        gradeType.name(GradeTypeName.INDIVIDUAL_PERFORMANCE.displayName());

        when(studentRepository.findByTeam(teamId)).thenReturn(students);
        when(gradeTypeService.findByName(GradeTypeName.INDIVIDUAL_PERFORMANCE.displayName(), projectId)).thenReturn(gradeType);
        when(gradeRepository.findIsConfirmedBySprindAndStudent(sprintId, student.id(), gradeType.id())).thenReturn(null);

        Boolean result = gradeService.setGradesConfirmation(teamId, sprintId, projectId);

        assertFalse(result);
    }

    @Test
    void isSameGradeShouldReturnTrueWhenGradesAreSame() {
        Grade grade = new Grade();
        grade.sprintId(1);
        grade.gradeTypeId(1);
        grade.studentId(1);

        Sprint sprint = new Sprint();
        sprint.id(1);
        GradeType gradeType = new GradeType();
        gradeType.id(1);
        Student student = new Student();
        student.id(1);

        Grade ratedGrade = new Grade();
        ratedGrade.sprint(sprint);
        ratedGrade.gradeType(gradeType);
        ratedGrade.student(student);

        assertTrue(gradeService.isSameGrade(grade, ratedGrade));
    }


    @ParameterizedTest
    @MethodSource("provideGradesForTesting")
    void isSameGradeShouldReturnFalseWhenDifferentIds(Grade grade, Grade ratedGrade) {
        assertFalse(gradeService.isSameGrade(grade, ratedGrade));
    }

    private static Stream<Arguments> provideGradesForTesting() {
        return Stream.of(
                Arguments.of(
                        createGrade(1, 1, 1),
                        createGrade(2, 1, 1)
                ),
                Arguments.of(
                        createGrade(1, 1, 1),
                        createGrade(1, 2, 1)
                ),
                Arguments.of(
                        createGrade(1, 1, 1),
                        createGrade(1, 1, 2)
                )
        );
    }

    private static Grade createGrade(int sprintId, int gradeTypeId, int studentId) {
        Grade grade = new Grade();
        grade.sprintId(sprintId);
        grade.gradeTypeId(gradeTypeId);
        grade.studentId(studentId);

        Sprint sprint = new Sprint();
        sprint.id(sprintId);
        GradeType gradeType = new GradeType();
        gradeType.id(gradeTypeId);
        Student student = new Student();
        student.id(studentId);

        Grade ratedGrade = new Grade();
        ratedGrade.sprint(sprint);
        ratedGrade.gradeType(gradeType);
        ratedGrade.student(student);

        return ratedGrade;
    }

    @Test
    void isSameGradeShouldReturnTrueWhenTeamIdsAreSameAndStudentIsNull() {
        Grade grade = new Grade();
        grade.sprintId(1);
        grade.gradeTypeId(1);
        grade.teamId(1);

        Sprint sprint = new Sprint();
        sprint.id(1);
        GradeType gradeType = new GradeType();
        gradeType.id(1);
        Team team = new Team();
        team.id(1);

        Grade ratedGrade = new Grade();
        ratedGrade.sprint(sprint);
        ratedGrade.gradeType(gradeType);
        ratedGrade.team(team);

        assertTrue(gradeService.isSameGrade(grade, ratedGrade));
    }

    @Test
    void isSameGradeShouldReturnFalseWhenTeamIdsAreDifferentAndStudentIsNull() {
        Grade grade = new Grade();
        grade.sprintId(1);
        grade.gradeTypeId(1);
        grade.teamId(1);

        Sprint sprint = new Sprint();
        sprint.id(1);
        GradeType gradeType = new GradeType();
        gradeType.id(1);
        Team team = new Team();
        team.id(2);

        Grade ratedGrade = new Grade();
        ratedGrade.sprint(sprint);
        ratedGrade.gradeType(gradeType);
        ratedGrade.team(team);

        assertFalse(gradeService.isSameGrade(grade, ratedGrade));
    }

    @Test
    void validateGradeShouldThrowExceptionWhenBothTeamAndStudentAreNull() {
        Grade grade = new Grade();

        assertThrows(IllegalArgumentException.class, () -> gradeService.validateGrade(grade));
    }

    @Test
    void updateGradeShouldUpdateGradeWhenValidGradeProvided() {
        Grade updatedGrade = new Grade();
        updatedGrade.value(90f);
        updatedGrade.comment("Good work");
        updatedGrade.sprintId(1);
        updatedGrade.authorId(1);
        updatedGrade.studentId(1);

        when(gradeRepository.findById(anyInt())).thenReturn(Optional.of(new Grade()));
        when(sprintService.getSprintById(anyInt())).thenReturn(new Sprint());
        when(userService.getUserById(anyInt())).thenReturn(new User());
        when(studentService.getStudentById(anyInt())).thenReturn(new Student());

        gradeService.updateGrade(1, updatedGrade);

        verify(gradeRepository, times(1)).save(any(Grade.class));
    }

    @Test
    void updateGradeShouldThrowIllegalArgumentExceptionWhenBothTeamAndStudentAreNotNull() {
        Grade updatedGrade = new Grade();
        updatedGrade.teamId(1);
        updatedGrade.studentId(1);

        when(gradeRepository.findById(anyInt())).thenReturn(Optional.of(new Grade()));
        when(teamService.getTeamById(anyInt())).thenReturn(new Team());
        when(studentService.getStudentById(anyInt())).thenReturn(new Student());

        assertThrows(IllegalArgumentException.class, () -> gradeService.updateGrade(1, updatedGrade));
    }

    @Test
    void updateGradeShouldThrowIllegalArgumentExceptionWhenBothTeamAndStudentAreNull() {
        Grade updatedGrade = new Grade();

        when(gradeRepository.findById(anyInt())).thenReturn(Optional.of(new Grade()));

        assertThrows(IllegalArgumentException.class, () -> gradeService.updateGrade(1, updatedGrade));
    }

    @Test
    void getRatedGradesByAuthorIdShouldReturnGradesWhenAuthorIdExists() {
        Integer authorId = 1;
        List<Grade> expectedGrades = Arrays.asList(new Grade(), new Grade());

        when(gradeRepository.findAllByAuthorId(authorId)).thenReturn(expectedGrades);

        List<Grade> actualGrades = gradeService.getRatedGradesByAuthorId(authorId);

        assertEquals(expectedGrades, actualGrades);
    }

    @Test
    void getRatedGradesByAuthorIdShouldReturnEmptyListWhenAuthorIdDoesNotExist() {
        Integer authorId = 1;

        when(gradeRepository.findAllByAuthorId(authorId)).thenReturn(Collections.emptyList());

        List<Grade> actualGrades = gradeService.getRatedGradesByAuthorId(authorId);

        assertTrue(actualGrades.isEmpty());
    }

    @Test
    void checkForExistingGradeShouldThrowExceptionWhenGradeExists() {
        Student student = new Student();
        student.id(1);
        Sprint sprint = new Sprint();
        sprint.id(1);
        GradeType gradeType = new GradeType();
        gradeType.id(1);
        Grade grade = new Grade();
        grade.sprint(sprint);

        grade.authorId(1);
        grade.sprintId(1);
        grade.gradeTypeId(1);
        grade.studentId(1);
        grade.gradeType(gradeType);
        grade.student(student);

        Grade existingGrade = new Grade();
        existingGrade.authorId(1);
        existingGrade.sprint(sprint);
        existingGrade.gradeTypeId(1);
        existingGrade.studentId(1);
        existingGrade.gradeType(gradeType);
        existingGrade.student(student);

        when(gradeRepository.findAllByAuthorId(grade.authorId())).thenReturn(Collections.singletonList(existingGrade));

        assertThrows(IllegalArgumentException.class, () -> gradeService.checkForExistingGrade(grade));
    }

    @Test
    void checkForExistingGradeShouldNotThrowExceptionWhenGradeExists() {
        Student student = new Student();
        student.id(1);
        Sprint sprint = new Sprint();
        sprint.id(1);
        GradeType gradeType = new GradeType();
        gradeType.id(1);


        Grade existingGrade = new Grade();
        existingGrade.authorId(1);
        existingGrade.sprint(sprint);
        existingGrade.gradeTypeId(1);
        existingGrade.studentId(1);
        existingGrade.gradeType(gradeType);
        existingGrade.student(student);

        assertDoesNotThrow(() -> gradeService.checkForExistingGrade(existingGrade));
    }

    @Test
    void testUpdateImportedMean() {
        Integer projectId = 1;

        // Mock students
        Student student1 = mock(Student.class);
        Student student2 = mock(Student.class);
        when(student1.id()).thenReturn(1);
        when(student2.id()).thenReturn(2);
        when(student1.bachelor()).thenReturn(false);
        when(student2.bachelor()).thenReturn(true); // Bachelor student to be skipped
        when(studentRepository.findAllByProject(projectId)).thenReturn(List.of(student1, student2));

        // Mock grades
        Grade grade1 = mock(Grade.class);
        Grade grade2 = mock(Grade.class);
        GradeType gradeType1 = mock(GradeType.class);
        GradeType gradeType2 = mock(GradeType.class);
        when(grade1.student()).thenReturn(student1);
        when(grade2.student()).thenReturn(student2);
        when(grade1.gradeType()).thenReturn(gradeType1);
        when(grade2.gradeType()).thenReturn(gradeType2);
        when(gradeType1.imported()).thenReturn(true);
        when(gradeType1.name()).thenReturn(GradeTypeName.AVERAGE.displayName());
        when(gradeType2.imported()).thenReturn(true);
        when(gradeType2.name()).thenReturn("OtherGrade");
        when(gradeRepository.findAll()).thenReturn(List.of(grade1, grade2));

        // Call the method to test
        gradeService.updateImportedMean(projectId);

        // Verify methods were called
        verify(studentRepository).findAllByProject(projectId);
        verify(gradeRepository).findAll();

    }

    @Test
    void getGradeByStudentAndGradeTypeShouldReturnGradeWhenExists() {
        Student student = new Student();
        student.name("John Doe");
        GradeType gradeType = new GradeType();
        gradeType.name("Test Grade");
        Float expectedGrade = 85.0f;

        when(gradeRepository.findValueByStudentAndGradeType(student, gradeType)).thenReturn(expectedGrade);

        Float actualGrade = gradeService.getGradeByStudentAndGradeType(student, gradeType);

        assertEquals(expectedGrade, actualGrade);
    }

    @Test
    void getGradeByStudentAndGradeTypeShouldReturnNullWhenNoGradeExists() {
        Student student = new Student();
        student.name("John Doe");
        GradeType gradeType = new GradeType();
        gradeType.name("Test Grade");

        when(gradeRepository.findValueByStudentAndGradeType(student, gradeType)).thenReturn(null);

        Float actualGrade = gradeService.getGradeByStudentAndGradeType(student, gradeType);

        assertNull(actualGrade);
    }

    @Test
    void getGradeByStudentAndGradeTypeShouldReturnNullWhenExceptionOccurs() {
        Student student = new Student();
        student.name("John Doe");
        GradeType gradeType = new GradeType();
        gradeType.name("Test Grade");

        when(gradeRepository.findValueByStudentAndGradeType(student, gradeType)).thenThrow(NullPointerException.class);

        Float actualGrade = gradeService.getGradeByStudentAndGradeType(student, gradeType);

        assertNull(actualGrade);
    }

    @Test
    void getGradesConfirmationShouldReturnTrueWhenGradeNotConfirmed() {
        Integer sprintId = 1;
        Integer teamId = 1;
        Integer projectId = 1;
        Student student = new Student();
        student.id(1);
        GradeType gradeType = new GradeType();
        gradeType.id(1);
        Grade grade = new Grade();
        grade.confirmed(false);

        when(studentRepository.findByTeam(teamId)).thenReturn(Collections.singletonList(student));
        when(gradeTypeRepository.findByNameAndProjectId(GradeTypeName.INDIVIDUAL_PERFORMANCE.displayName(), projectId)).thenReturn(gradeType);
        when(gradeRepository.findIsConfirmedBySprindAndStudent(sprintId, student.id(), gradeType.id())).thenReturn(grade);

        Boolean result = gradeService.getGradesConfirmation(sprintId, teamId, projectId);

        assertTrue(result);
    }

    @Test
    void getIndividualGradesByTeamShouldReturnGradesWhenExist() {
        Integer sprintId = 1;
        Integer teamId = 1;
        List<Grade> expectedGrades = Arrays.asList(new Grade(), new Grade());

        when(gradeRepository.findIndividualGradesByTeam(sprintId, teamId)).thenReturn(expectedGrades);

        List<Grade> actualGrades = gradeService.getIndividualGradesByTeam(sprintId, teamId);

        assertEquals(expectedGrades, actualGrades);
    }

    @Test
    void getIndividualGradesByTeamShouldReturnEmptyListWhenNoGradesExist() {
        Integer sprintId = 1;
        Integer teamId = 1;

        when(gradeRepository.findIndividualGradesByTeam(sprintId, teamId)).thenReturn(Collections.emptyList());

        List<Grade> actualGrades = gradeService.getIndividualGradesByTeam(sprintId, teamId);

        assertTrue(actualGrades.isEmpty());
    }

    @Test
    void setGradesConfirmationShouldReturnTrueWhenGradesExistAndAreNotConfirmed() {
        Integer sprintId = 1;
        Integer teamId = 1;
        Integer projectId = 1;
        Student student = new Student();
        student.id(1);
        GradeType gradeType = new GradeType();
        gradeType.id(1);
        Grade grade = new Grade();
        grade.confirmed(false);

        when(studentRepository.findByTeam(teamId)).thenReturn(Collections.singletonList(student));
        when(gradeTypeRepository.findByNameAndProjectId(GradeTypeName.INDIVIDUAL_PERFORMANCE.displayName(), projectId)).thenReturn(gradeType);
        when(gradeRepository.findIsConfirmedBySprindAndStudents(sprintId, student.id(), gradeType.id())).thenReturn(Collections.singletonList(grade));

        Boolean result = gradeService.setGradesConfirmation(sprintId, teamId, projectId);

        assertTrue(result);
        verify(gradeRepository, times(1)).setConfirmedBySprintAndStudent(grade.id());
    }

    @Test
    void setGradesConfirmationShouldReturnTrueWhenGradesExistAndAreConfirmed() {
        Integer sprintId = 1;
        Integer teamId = 1;
        Integer projectId = 1;
        Student student = new Student();
        student.id(1);
        GradeType gradeType = new GradeType();
        gradeType.id(1);
        Grade grade = new Grade();
        grade.confirmed(true);

        when(studentRepository.findByTeam(teamId)).thenReturn(Collections.singletonList(student));
        when(gradeTypeRepository.findByNameAndProjectId(GradeTypeName.INDIVIDUAL_PERFORMANCE.displayName(), projectId)).thenReturn(gradeType);
        when(gradeRepository.findIsConfirmedBySprindAndStudents(sprintId, student.id(), gradeType.id())).thenReturn(Collections.singletonList(grade));

        Boolean result = gradeService.setGradesConfirmation(sprintId, teamId, projectId);

        assertTrue(result);
        verify(gradeRepository, never()).setConfirmedBySprintAndStudent(anyInt());
    }

}