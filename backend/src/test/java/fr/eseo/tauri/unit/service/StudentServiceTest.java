package fr.eseo.tauri.unit.service;

import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import fr.eseo.tauri.exception.EmptyResourceException;
import fr.eseo.tauri.exception.ResourceNotFoundException;
import fr.eseo.tauri.model.*;
import fr.eseo.tauri.model.enumeration.Gender;
import fr.eseo.tauri.model.enumeration.GradeTypeName;
import fr.eseo.tauri.repository.BonusRepository;
import fr.eseo.tauri.repository.CommentRepository;
import fr.eseo.tauri.repository.GradeRepository;
import fr.eseo.tauri.repository.StudentRepository;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Nested
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private GradeService gradeService;

    @Mock
    private TeamService teamService;

    @Mock
    private ProjectService projectService;

    @Mock
    private SprintService sprintService;

    @Mock
    private RoleService roleService;

    @Mock
    private BonusRepository bonusRepository;

    @Mock
    private UserService userService;

    @Mock
    private GradeTypeService gradeTypeService;

    @Mock
    private GradeRepository gradeRepository;

    @Mock
    private BonusService bonusService;

    @Mock
    private PresentationOrderService presentationOrderService;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private StudentService studentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getStudentByIdShouldReturnStudentWhenAuthorizedAndIdExists() {
        Student student = new Student();
        when(studentRepository.findById(anyInt())).thenReturn(Optional.of(student));

        Student result = studentService.getStudentById(1);

        assertEquals(student, result);
    }

    @Test
    void getStudentByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        when(studentRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> studentService.getStudentById(1));
    }

    @Test
    void getAllStudentsByProjectShouldReturnStudentsWhenAuthorized() {
        when(studentRepository.findAllByProject(anyInt())).thenReturn(Arrays.asList(new Student(), new Student()));

        List<Student> result = studentService.getAllStudentsByProject(1);

        assertEquals(2, result.size());
    }

    @Test
    void getAllStudentsByProjectShouldReturnEmptyListWhenNoStudentsExist() {
        when(studentRepository.findAllByProject(anyInt())).thenReturn(Collections.emptyList());

        List<Student> result = studentService.getAllStudentsByProject(1);

        assertTrue(result.isEmpty());
    }

    @Test
    void createStudentShouldSaveWhenAuthorizedAndProjectAndTeamExist() {
        when(projectService.getProjectById(anyInt())).thenReturn(new Project());
        when(teamService.getTeamById(anyInt())).thenReturn(new Team());
        when(sprintService.getAllSprintsByProject(anyInt())).thenReturn(Collections.emptyList());

        Student student = new Student();
        student.projectId(1);
        student.teamId(1);
        studentService.createStudent(student);

        verify(studentRepository, times(1)).save(student);
        verify(roleService, times(1)).createRole(any(Role.class));
    }

    @Test
    void extractNamesGenderBachelorAndGradesShouldReturnCorrectData() throws IOException, CsvValidationException {
        String csvContent = "1,John Doe,M,B,15,14,13\n2,Jane Doe,F,B,12,13,14";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());

        Map<String, Object> result = studentService.extractNamesGenderBachelorAndGrades(inputStream);

        List<String> names = (List<String>) result.get(StudentService.MAP_KEY_NAMES);
        List<String> genders = (List<String>) result.get(StudentService.MAP_KEY_GENDERS);
        List<String> bachelors = (List<String>) result.get(StudentService.MAP_KEY_BACHELORS);
        List<List<String>> grades = (List<List<String>>) result.get(StudentService.MAP_KEY_GRADES);

        assertEquals(Arrays.asList("John Doe", "Jane Doe"), names);
        assertEquals(Arrays.asList("M", "F"), genders);
        assertEquals(Arrays.asList("B", "B"), bachelors);
        assertEquals(Arrays.asList(Arrays.asList("15", "14", "13"), Arrays.asList("12", "13", "14")), grades);
    }

    @Test
    void extractNamesGenderBachelorAndGradesShouldHandleEmptyInputStream() throws IOException, CsvValidationException {
        InputStream inputStream = new ByteArrayInputStream("".getBytes());

        Map<String, Object> result = studentService.extractNamesGenderBachelorAndGrades(inputStream);

        List<String> names = (List<String>) result.get(StudentService.MAP_KEY_NAMES);
        List<String> genders = (List<String>) result.get(StudentService.MAP_KEY_GENDERS);
        List<String> bachelors = (List<String>) result.get(StudentService.MAP_KEY_BACHELORS);
        List<List<String>> grades = (List<List<String>>) result.get(StudentService.MAP_KEY_GRADES);

        assertTrue(names.isEmpty());
        assertTrue(genders.isEmpty());
        assertTrue(bachelors.isEmpty());
        assertTrue(grades.isEmpty());
    }

    @Test
    void extractNamesGenderBachelorAndGradesShouldHandleInputStreamWithMissingData() throws IOException, CsvValidationException {
        String csvContent = "1,John Doe,M,,15,14,13\n2,Jane Doe,,B,12,13,14";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());

        Map<String, Object> result = studentService.extractNamesGenderBachelorAndGrades(inputStream);

        List<String> names = (List<String>) result.get(StudentService.MAP_KEY_NAMES);
        List<String> genders = (List<String>) result.get(StudentService.MAP_KEY_GENDERS);
        List<String> bachelors = (List<String>) result.get(StudentService.MAP_KEY_BACHELORS);
        List<List<String>> grades = (List<List<String>>) result.get(StudentService.MAP_KEY_GRADES);

        assertEquals(Arrays.asList("John Doe", "Jane Doe"), names);
        assertEquals(Arrays.asList("M", ""), genders);
        assertEquals(Arrays.asList("", "B"), bachelors);
        assertEquals(Arrays.asList(Arrays.asList("15", "14", "13"), Arrays.asList("12", "13", "14")), grades);
    }

    @Test
    void hasNonEmptyValueReturnsTrueWhenIndexContainsNonEmptyValue() {
        String[] line = {"test", "example"};
        assertTrue(StudentService.hasNonEmptyValue(line, 0));
    }

    @Test
    void hasNonEmptyValueReturnsFalseWhenIndexContainsEmptyValue() {
        String[] line = {"", "test", "example"};
        assertFalse(StudentService.hasNonEmptyValue(line, 0));
    }

    @Test
    void hasNonEmptyValueReturnsFalseWhenIndexIsOutOfBounds() {
        String[] line = {"test", "example"};
        assertFalse(StudentService.hasNonEmptyValue(line, 3));
    }

    @Test
    void hasNonEmptyValueReturnsFalseWhenLineIsEmpty() {
        String[] line = {};
        assertFalse(StudentService.hasNonEmptyValue(line, 0));
    }


    @Test
    void createStudentFromDataShouldThrowExceptionWhenNameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> studentService.createStudentFromData(null, "M", "B", 1));
    }

    @Test
    void createStudentFromDataShouldThrowExceptionWhenNameIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> studentService.createStudentFromData("", "M", "B", 1));
    }

    @Test
    void createStudentFromDataShouldThrowExceptionWhenGenderIsNull() {
        assertThrows(IllegalArgumentException.class, () -> studentService.createStudentFromData("John Doe", null, "B", 1));
    }

    @Test
    void createStudentFromDataShouldThrowExceptionWhenGenderIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> studentService.createStudentFromData("John Doe", "", "B", 1));
    }

    @Test
    void createStudentFromDataShouldThrowExceptionWhenBachelorIsNull() {
        assertThrows(IllegalArgumentException.class, () -> studentService.createStudentFromData("John Doe", "M", null, 1));
    }

    @Test
    void populateDatabaseFromCSVShouldThrowEmptyResourceExceptionWhenFileIsEmpty() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);

        assertThrows(EmptyResourceException.class, () -> studentService.populateDatabaseFromCSV(file, 1));
    }

    @Test
    void writeHeadersShouldSkipAverageGradeType() {
        StringWriter stringWriter = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(stringWriter);

        GradeType gradeType1 = new GradeType();
        gradeType1.name("GradeType1");
        gradeType1.factor(1.0f);

        GradeType gradeType2 = new GradeType();
        gradeType2.name(GradeTypeName.AVERAGE.displayName());
        gradeType2.factor(2.0f);

        studentService.writeHeaders(csvWriter, Arrays.asList(gradeType1, gradeType2));

        String expectedCsv = """
                "","","","","","1.0"
                "","","sexe M / F","","","GradeType1"
                """;
        String actualCsv = stringWriter.toString();

        assertEquals(expectedCsv, actualCsv);
    }

    @Test
    void writeHeadersShouldHandleEmptyGradeTypes() {
        StringWriter stringWriter = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(stringWriter);

        studentService.writeHeaders(csvWriter, Collections.emptyList());

        String expectedCsv = """
                "","","",""
                "","","sexe M / F",""
                """;
        String actualCsv = stringWriter.toString();

        assertEquals(expectedCsv, actualCsv);
    }

    @Test
    void writeStudentDataShouldWriteCorrectDataForSingleStudent() {
        StringWriter stringWriter = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(stringWriter);

        Student student = new Student();
        student.name("John Doe");
        student.gender(Gender.MAN);
        student.bachelor(true);

        GradeType gradeType = new GradeType();
        gradeType.name("GradeType1");

        when(gradeService.getGradeByStudentAndGradeType(student, gradeType)).thenReturn(15.0f);

        studentService.writeStudentData(csvWriter, List.of(student), List.of(gradeType));

        String expectedCsv = "\"1\",\"John Doe\",\"M\",\"B\",\"15.0\"\n";
        String actualCsv = stringWriter.toString();

        assertEquals(expectedCsv, actualCsv);
    }

    @Test
    void writeStudentDataShouldWriteCorrectDataForMultipleStudents() {
        StringWriter stringWriter = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(stringWriter);

        Student student1 = new Student();
        student1.name("John Doe");
        student1.gender(Gender.MAN);
        student1.bachelor(true);

        Student student2 = new Student();
        student2.name("Jane Doe");
        student2.gender(Gender.WOMAN);
        student2.bachelor(false);

        GradeType gradeType = new GradeType();
        gradeType.name("GradeType1");

        when(gradeService.getGradeByStudentAndGradeType(student1, gradeType)).thenReturn(15.0f);
        when(gradeService.getGradeByStudentAndGradeType(student2, gradeType)).thenReturn(14.0f);

        studentService.writeStudentData(csvWriter, Arrays.asList(student1, student2), List.of(gradeType));

        String expectedCsv = """
                "1","John Doe","M","B","15.0"
                "2","Jane Doe","F","","14.0"
                """;
        String actualCsv = stringWriter.toString();

        assertEquals(expectedCsv, actualCsv);
    }

    @Test
    void writeStudentDataShouldHandleNullGrade() {
        StringWriter stringWriter = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(stringWriter);

        Student student = new Student();
        student.name("John Doe");
        student.gender(Gender.MAN);
        student.bachelor(true);

        GradeType gradeType = new GradeType();
        gradeType.name("GradeType1");

        when(gradeService.getGradeByStudentAndGradeType(student, gradeType)).thenReturn(null);

        studentService.writeStudentData(csvWriter, List.of(student), List.of(gradeType));

        String expectedCsv = "\"1\",\"John Doe\",\"M\",\"B\",\"\"\n";
        String actualCsv = stringWriter.toString();

        assertEquals(expectedCsv, actualCsv);
    }

    @Test
    void writeSummaryDataShouldWriteCorrectDataWithValidInput() {
        StringWriter stringWriter = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(stringWriter);

        when(studentRepository.countWomen()).thenReturn(5);
        when(studentRepository.countTotal()).thenReturn(10);
        when(studentRepository.countBachelor()).thenReturn(3);

        studentService.writeSummaryData(csvWriter, 2);

        String expectedCsv = """
                "","","","","",""
                "","","","","",""
                "","","","","",""
                "","","","","",""
                "","Nombre F","5","","",""
                "","Nombre M","5","","",""
                "","Nombre B","","3","",""
                """;
        String actualCsv = stringWriter.toString();

        assertEquals(expectedCsv, actualCsv);
    }

    @Test
    void writeSummaryDataShouldHandleZeroGrades() {
        StringWriter stringWriter = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(stringWriter);

        when(studentRepository.countWomen()).thenReturn(0);
        when(studentRepository.countTotal()).thenReturn(0);
        when(studentRepository.countBachelor()).thenReturn(0);

        studentService.writeSummaryData(csvWriter, 0);

        String expectedCsv = """
                "","","",""
                "","","",""
                "","","",""
                "","","",""
                "","Nombre F","0",""
                "","Nombre M","0",""
                "","Nombre B","","0"
                """;
        String actualCsv = stringWriter.toString();

        assertEquals(expectedCsv, actualCsv);
    }

    @ParameterizedTest
    @MethodSource("provideParametersForWriteEmptyRowsTest")
    void writeEmptyRowsTest(int rows, int rowLength, String expectedCsv) {
        StringWriter stringWriter = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(stringWriter);

        studentService.writeEmptyRows(csvWriter, rows, rowLength);

        String actualCsv = stringWriter.toString();

        assertEquals(expectedCsv, actualCsv);
    }

    private static Stream<Arguments> provideParametersForWriteEmptyRowsTest() {
        return Stream.of(
                Arguments.of(3, 2, "\"\",\"\"\n\"\",\"\"\n\"\",\"\"\n"),
                Arguments.of(0, 2, ""),
                Arguments.of(3, 0, "\n\n\n")
        );
    }


    @ParameterizedTest
    @MethodSource("provideParametersForWriteCountRowTest")
    void writeCountRowTest(String label, int count, String expectedCsv) {
        StringWriter stringWriter = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(stringWriter);

        studentService.writeCountRow(csvWriter, label, count, 3);

        String actualCsv = stringWriter.toString();

        assertEquals(expectedCsv, actualCsv);
    }

    private static Stream<Arguments> provideParametersForWriteCountRowTest() {
        return Stream.of(
                Arguments.of("Nombre F", 5, "\"\",\"Nombre F\",\"5\"\n"),
                Arguments.of("Nombre F", 0, "\"\",\"Nombre F\",\"0\"\n"),
                Arguments.of("", 5, "\"\",\"\",\"5\"\n")
        );
    }

    @Test
    void getStudentBonusShouldReturnBonusWhenAuthorizedAndBonusExists() {
        Integer idStudent = 1;
        Boolean limited = true;
        Bonus bonus = new Bonus();
        Integer idSprint = 1;

        when(bonusRepository.findStudentBonus(idStudent, limited, idSprint)).thenReturn(bonus);

        Bonus result = studentService.getStudentBonus(idStudent, limited, idSprint);

        assertEquals(bonus, result);
    }

    @Test
    void getStudentBonusShouldReturnNullWhenNoBonusFound() {
        Integer idStudent = 1;
        Boolean limited = true;
        Integer idSprint = 1;

        when(bonusRepository.findStudentBonus(idStudent, limited, idSprint)).thenReturn(null);

        Bonus result = studentService.getStudentBonus(idStudent, limited, idSprint);

        assertNull(result);
    }

    @Test
    void getStudentBonusesShouldReturnBonusesWhenAuthorizedAndBonusesExist() {
        Integer idStudent = 1;
        List<Bonus> bonuses = Arrays.asList(new Bonus(), new Bonus());
        Integer idSprint = 1;

        when(bonusRepository.findAllStudentBonuses(idStudent, idSprint)).thenReturn(bonuses);

        List<Bonus> result = studentService.getStudentBonuses(idStudent, idSprint);

        assertEquals(bonuses, result);
    }

    @Test
    void getStudentBonusesShouldReturnEmptyListWhenNoBonusesFound() {
        Integer idStudent = 1;
        Integer idSprint = 1;

        when(bonusRepository.findAllStudentBonuses(idStudent, idSprint)).thenReturn(Collections.emptyList());

        List<Bonus> result = studentService.getStudentBonuses(idStudent, idSprint);

        assertTrue(result.isEmpty());
    }

    @Test
    void updateStudentShouldUpdateGenderWhenProvided() {
        Integer id = 1;
        Student existingStudent = new Student();
        Student updatedStudent = new Student();
        updatedStudent.gender(Gender.MAN);

        when(studentRepository.findById(id)).thenReturn(Optional.of(existingStudent));

        studentService.updateStudent(id, updatedStudent);

        assertEquals(updatedStudent.gender(), existingStudent.gender());
        verify(studentRepository, times(1)).save(existingStudent);
    }

    @Test
    void updateStudentShouldUpdateTeamRoleWhenProvided() {
        Integer id = 1;
        Student existingStudent = new Student();
        Student updatedStudent = new Student();
        updatedStudent.teamRole("NewRole");

        when(studentRepository.findById(id)).thenReturn(Optional.of(existingStudent));

        studentService.updateStudent(id, updatedStudent);

        assertEquals(updatedStudent.teamRole(), existingStudent.teamRole());
        verify(studentRepository, times(1)).save(existingStudent);
    }

    @Test
    void updateStudentShouldUpdateProjectWhenProjectIdIsProvided() {
        Integer id = 1;
        Student existingStudent = new Student();
        Student updatedStudent = new Student();
        updatedStudent.projectId(2);
        Project project = new Project();
        project.id(2);

        when(studentRepository.findById(id)).thenReturn(Optional.of(existingStudent));
        when(projectService.getProjectById(updatedStudent.projectId())).thenReturn(project);

        studentService.updateStudent(id, updatedStudent);

        assertEquals(project, existingStudent.project());
        verify(studentRepository, times(1)).save(existingStudent);
    }

    @Test
    void updateStudentShouldUpdateTeamWhenTeamIdIsProvided() {
        Integer id = 1;
        Student existingStudent = new Student();
        Student updatedStudent = new Student();
        updatedStudent.teamId(2);
        Team team = new Team();
        team.id(2);

        when(studentRepository.findById(id)).thenReturn(Optional.of(existingStudent));
        when(teamService.getTeamById(updatedStudent.teamId())).thenReturn(team);

        studentService.updateStudent(id, updatedStudent);

        assertEquals(team, existingStudent.team());
        verify(studentRepository, times(1)).save(existingStudent);
    }

    @Test
    void updateStudentShouldThrowResourceNotFoundExceptionWhenStudentDoesNotExist() {
        Integer id = 1;
        Student updatedStudent = new Student();

        when(studentRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> studentService.updateStudent(id, updatedStudent));
    }

    @Test
    void deleteStudentShouldDeleteStudentWhenStudentExists() {
        Integer id = 1;
        Student existingStudent = new Student();

        when(studentRepository.findById(id)).thenReturn(Optional.of(existingStudent));

        studentService.deleteStudent(id);

        verify(studentRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteStudentShouldThrowResourceNotFoundExceptionWhenStudentDoesNotExist() {
        Integer id = 1;

        when(studentRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> studentService.deleteStudent(id));
    }

    @Test
    void deleteAllStudentsByProjectShouldDeleteAllStudentsWhenStudentsExist() {
        Integer projectId = 1;
        Student student1 = new Student();
        student1.id(1);
        Student student2 = new Student();
        student2.id(2);
        List<Student> students = Arrays.asList(student1, student2);

        when(studentRepository.findAllByProject(projectId)).thenReturn(students);

        studentService.deleteAllStudentsByProject(projectId);

        verify(userService, times(1)).deleteUserById(student1.id());
        verify(userService, times(1)).deleteUserById(student2.id());
        verify(gradeTypeService, times(1)).deleteAllImportedGradeTypes();
        verify(teamService, times(1)).deleteAllTeamsByProject(projectId);
    }

    @Test
    void deleteAllStudentsByProjectShouldNotDeleteAnyStudentWhenNoStudentsExist() {
        Integer projectId = 1;

        when(studentRepository.findAllByProject(projectId)).thenReturn(Collections.emptyList());

        studentService.deleteAllStudentsByProject(projectId);

        verify(userService, times(0)).deleteUserById(anyInt());
        verify(gradeTypeService, times(1)).deleteAllImportedGradeTypes();
        verify(teamService, times(1)).deleteAllTeamsByProject(projectId);
    }

    @Test
    void createStudentsCSVShouldReturnByteArrayWhenStudentsExist() throws IOException {
        Integer projectId = 1;
        Student student = new Student();
        student.projectId(projectId);
        student.gender(Gender.MAN);
        List<Student> students = Collections.singletonList(student);

        when(gradeTypeService.getAllImportedGradeTypes(projectId)).thenReturn(Collections.emptyList());
        when(studentService.getAllStudentsByProject(projectId)).thenReturn(students);

        byte[] result = studentService.createStudentsCSV(projectId);

        assertNotNull(result);
        assertEquals(157, result.length);
    }

    @Test
    void getIndividualTotalGradeShouldReturnCorrectGradeWhenGradesExist() {
        Integer id = 1;
        Integer sprintId = 1;
        Integer teamId = 1;
        Double expectedGrade = 16.666666666666668;

        when(studentRepository.findById(id)).thenReturn(Optional.of(new Student().projectId(1)));
        when(userService.getTeamByMemberId(id, studentService.getStudentById(id).projectId())).thenReturn(new Team().id(teamId));
        when(gradeRepository.findAverageByGradeTypeForTeam(teamId, sprintId, GradeTypeName.GLOBAL_TEAM_PERFORMANCE.displayName())).thenReturn(10.0);
        when(gradeRepository.findAverageByGradeTypeForStudent(id, sprintId, GradeTypeName.INDIVIDUAL_PERFORMANCE.displayName())).thenReturn(20.0);

        Double result = studentService.getIndividualTotalGrade(id, sprintId);

        assertEquals(expectedGrade, result);
    }

    @Test
    void getSprintGradeShouldReturnCorrectGradeWhenBonusesAndGradesExist() {
        Integer studentId = 1;
        Integer sprintId = 1;
        Integer teamId = 1;
        Double expectedGrade = 17.0;

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(new Student().projectId(1)));
        when(userService.getTeamByMemberId(studentId, studentService.getStudentById(studentId).projectId())).thenReturn(new Team().id(teamId));
        when(teamService.getTeamTotalGrade(teamId, sprintId)).thenReturn(20.0);
        when(studentService.getStudentBonuses(studentId, sprintId)).thenReturn(Arrays.asList(new Bonus().value(2.0F), new Bonus().value(3.0F)));
        when(studentService.getIndividualTotalGrade(studentId, sprintId)).thenReturn(15.0);

        Double result = studentService.getSprintGrade(studentId, sprintId);

        assertEquals(expectedGrade, result);
    }

    @Test
    void getSprintGradeShouldReturnCorrectGradeWhenNoBonusesExist() {
        Integer studentId = 1;
        Integer sprintId = 1;
        Integer teamId = 1;
        Double expectedGrade = 17.0;

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(new Student().projectId(1)));
        when(userService.getTeamByMemberId(studentId, studentService.getStudentById(studentId).projectId())).thenReturn(new Team().id(teamId));
        when(teamService.getTeamTotalGrade(teamId, sprintId)).thenReturn(20.0);
        when(studentService.getStudentBonuses(studentId, sprintId)).thenReturn(Collections.emptyList());
        when(studentService.getIndividualTotalGrade(studentId, sprintId)).thenReturn(15.0);

        Double result = studentService.getSprintGrade(studentId, sprintId);

        assertEquals(expectedGrade, result);
    }

    @Test
    void getSprintGradeShouldReturnCorrectGradeWhenTeamGradeExceedsTwentyWithBonuses() {
        Integer studentId = 1;
        Integer sprintId = 1;
        Integer teamId = 1;
        Double expectedGrade = 17.0;

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(new Student().projectId(1)));
        when(userService.getTeamByMemberId(studentId, studentService.getStudentById(studentId).projectId())).thenReturn(new Team().id(teamId));
        when(teamService.getTeamTotalGrade(teamId, sprintId)).thenReturn(25.0);
        when(studentService.getStudentBonuses(studentId, sprintId)).thenReturn(Arrays.asList(new Bonus().value(2.0F), new Bonus().value(3.0F)));
        when(studentService.getIndividualTotalGrade(studentId, sprintId)).thenReturn(15.0);

        Double result = studentService.getSprintGrade(studentId, sprintId);

        assertEquals(expectedGrade, result);
    }

    @Test
    void getGradeByTypeAndAuthorShouldReturnGradeWhenGradeExists() {
        Integer id = 1;
        Integer gradeTypeId = 1;
        Integer authorId = 1;
        Integer sprintId = 1;
        Grade expectedGrade = new Grade();

        when(gradeRepository.findByStudentAndGradeTypeAndAuthor(id, gradeTypeId, authorId, sprintId)).thenReturn(expectedGrade);

        Grade result = studentService.getGradeByTypeAndAuthor(id, gradeTypeId, authorId, sprintId);

        assertEquals(expectedGrade, result);
    }

    @Test
    void getGradeByTypeAndAuthorShouldReturnNullWhenGradeDoesNotExist() {
        Integer id = 1;
        Integer gradeTypeId = 1;
        Integer authorId = 1;
        Integer sprintId = 1;

        when(gradeRepository.findByStudentAndGradeTypeAndAuthor(id, gradeTypeId, authorId, sprintId)).thenReturn(null);

        Grade result = studentService.getGradeByTypeAndAuthor(id, gradeTypeId, authorId, sprintId);

        assertNull(result);
    }

    @Test
    void createStudentFromData_ShouldCreateStudent() {
        String name = "John Doe";
        String gender = "M";
        String bachelor = "yes";
        Integer projectId = 1;

        Project project = new Project();
        project.id(projectId);

        when(projectService.getProjectById(projectId)).thenReturn(project);

        Student student = studentService.createStudentFromData(name, gender, bachelor, projectId);

        assertNotNull(student);
        assertEquals(name, student.name());
        assertEquals(Gender.MAN, student.gender());
        assertTrue(student.bachelor());
        assertEquals(project, student.project());
        assertEquals("doe.john@reseau.eseo.fr", student.email());

        verify(projectService, times(1)).getProjectById(projectId);
    }

    @Test
    void createStudentFromData_ShouldThrowException_WhenNameIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            studentService.createStudentFromData(null, "M", "yes", 1);
        });
        assertEquals("Name cannot be null or empty", exception.getMessage());
    }

    @Test
    void createStudentFromData_ShouldThrowException_WhenNameIsEmpty() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            studentService.createStudentFromData("   ", "M", "yes", 1);
        });
        assertEquals("Name cannot be null or empty", exception.getMessage());
    }

    @Test
    void createStudentFromData_ShouldThrowException_WhenGenderIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            studentService.createStudentFromData("John Doe", null, "yes", 1);
        });
        assertEquals("Gender cannot be null or empty", exception.getMessage());
    }

    @Test
    void createStudentFromData_ShouldThrowException_WhenGenderIsEmpty() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            studentService.createStudentFromData("John Doe", "   ", "yes", 1);
        });
        assertEquals("Gender cannot be null or empty", exception.getMessage());
    }

    @Test
    void createStudentFromData_ShouldThrowException_WhenBachelorIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            studentService.createStudentFromData("John Doe", "M", null, 1);
        });
        assertEquals("Bachelor status cannot be null", exception.getMessage());
    }

    @Test
    void createStudent_ShouldCreateStudentAndAssignRolesAndBonuses() {
        Integer projectId = 1;
        Integer teamId = 1;
        Student student = new Student();
        student.projectId(projectId);
        student.teamId(teamId);

        Project project = new Project();
        project.id(projectId);

        Team team = new Team();
        team.id(teamId);

        Sprint sprint1 = new Sprint();
        Sprint sprint2 = new Sprint();
        sprint1.projectId(projectId);
        sprint2.projectId(projectId);

        List<Sprint> sprints = List.of(sprint1, sprint2);
        List<Team> teams = List.of(team);

        when(projectService.getProjectById(projectId)).thenReturn(project);
        when(teamService.getTeamById(teamId)).thenReturn(team);
        when(studentRepository.save(student)).thenReturn(student);
        when(sprintService.getAllSprintsByProject(projectId)).thenReturn(sprints);
        when(teamService.getAllTeamsByProject(projectId)).thenReturn(teams);

        studentService.createStudent(student);

        verify(studentRepository, times(1)).save(student);

        verify(roleService, times(1)).createRole(any(Role.class));

        verify(presentationOrderService, times(sprints.size())).createPresentationOrder(any(PresentationOrder.class));

        verify(bonusService, times(sprints.size() * 2)).createBonus(any(Bonus.class));
    }

    @Test
    void getFeedbacksByStudentAndSprintShouldReturnFeedbacksWhenTheyExist() {
        Integer studentId = 1;
        Integer sprintId = 1;
        List<Comment> expectedComments = Arrays.asList(new Comment(), new Comment());

        when(commentRepository.findAllByStudentIdAndSprintId(studentId, sprintId)).thenReturn(expectedComments);

        List<Comment> result = studentService.getFeedbacksByStudentAndSprint(studentId, sprintId);

        assertEquals(expectedComments, result);
    }

    @Test
    void getFeedbacksByStudentAndSprintShouldReturnEmptyListWhenNoFeedbacksExist() {
        Integer studentId = 1;
        Integer sprintId = 1;

        when(commentRepository.findAllByStudentIdAndSprintId(studentId, sprintId)).thenReturn(Collections.emptyList());

        List<Comment> result = studentService.getFeedbacksByStudentAndSprint(studentId, sprintId);

        assertTrue(result.isEmpty());
    }

    @Test
    void testPopulateDatabaseFromCSV_Success() throws IOException, CsvValidationException {
        String csvContent = "John Doe,M,B,15.5,16.0\nJane Smith,F,,18.0,17.5";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());

        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getInputStream()).thenReturn(inputStream);

        GradeType gradeType1 = new GradeType();
        GradeType gradeType2 = new GradeType();
        List<GradeType> gradeTypes = Arrays.asList(gradeType1, gradeType2);

        when(gradeTypeService.createGradeTypesFromCSV(any(InputStream.class))).thenReturn(gradeTypes);

        Map<String, Object> extractedData = new HashMap<>();
        extractedData.put(StudentService.MAP_KEY_NAMES, Arrays.asList("John Doe", "Jane Smith"));
        extractedData.put(StudentService.MAP_KEY_GENDERS, Arrays.asList("M", "F"));
        extractedData.put(StudentService.MAP_KEY_BACHELORS, Arrays.asList("B", ""));
        extractedData.put(StudentService.MAP_KEY_GRADES, Arrays.asList(
                Arrays.asList("15.5", "16.0"),
                Arrays.asList("18.0", "17.5")
        ));

        // Use spy to stub methods on the studentService instance
        StudentService spyStudentService = spy(studentService);
        doReturn(extractedData).when(spyStudentService).extractNamesGenderBachelorAndGrades(any(InputStream.class));

        Student student1 = new Student();
        Student student2 = new Student();

        doReturn(student1).when(spyStudentService).createStudentFromData("John Doe", "M", "B", 1);
        doReturn(student2).when(spyStudentService).createStudentFromData("Jane Smith", "F", "", 1);

        spyStudentService.populateDatabaseFromCSV(mockFile, 1);

        verify(gradeTypeService, times(1)).createGradeTypesFromCSV(any(InputStream.class));
        verify(spyStudentService, times(1)).extractNamesGenderBachelorAndGrades(any(InputStream.class));
        verify(spyStudentService, times(2)).createStudent(student1);
        verify(spyStudentService, times(2)).createStudent(student2);

        verify(gradeService, times(4)).createGrade(any(Grade.class)); // 4 grades in total
    }

    @Test
    void testPopulateDatabaseFromCSV_GradeParsingError() throws IOException, CsvValidationException {
        String csvContent = "John Doe,M,B,15.5,abc\nJane Smith,F,,18.0,17.5";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());

        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getInputStream()).thenReturn(inputStream);

        GradeType gradeType1 = new GradeType();
        GradeType gradeType2 = new GradeType();
        List<GradeType> gradeTypes = Arrays.asList(gradeType1, gradeType2);

        when(gradeTypeService.createGradeTypesFromCSV(any(InputStream.class))).thenReturn(gradeTypes);

        Map<String, Object> extractedData = new HashMap<>();
        extractedData.put(StudentService.MAP_KEY_NAMES, Arrays.asList("John Doe", "Jane Smith"));
        extractedData.put(StudentService.MAP_KEY_GENDERS, Arrays.asList("M", "F"));
        extractedData.put(StudentService.MAP_KEY_BACHELORS, Arrays.asList("B", ""));
        extractedData.put(StudentService.MAP_KEY_GRADES, Arrays.asList(
                Arrays.asList("15.5", "abc"),
                Arrays.asList("18.0", "17.5")
        ));

        // Use spy to stub methods on the studentService instance
        StudentService spyStudentService = spy(studentService);
        doReturn(extractedData).when(spyStudentService).extractNamesGenderBachelorAndGrades(any(InputStream.class));

        Student student1 = new Student();
        Student student2 = new Student();

        doReturn(student1).when(spyStudentService).createStudentFromData("John Doe", "M", "B", 1);
        doReturn(student2).when(spyStudentService).createStudentFromData("Jane Smith", "F", "", 1);

        spyStudentService.populateDatabaseFromCSV(mockFile, 1);

        verify(gradeTypeService, times(1)).createGradeTypesFromCSV(any(InputStream.class));
        verify(spyStudentService, times(1)).extractNamesGenderBachelorAndGrades(any(InputStream.class));
        verify(spyStudentService, times(2)).createStudent(student1);
        verify(spyStudentService, times(2)).createStudent(student2);

        // Ensure createGrade is called only for valid grades
        verify(gradeService, times(3)).createGrade(any(Grade.class)); // 3 valid grades in total
    }

}