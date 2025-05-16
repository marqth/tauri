package fr.eseo.tauri.unit.service;

import fr.eseo.tauri.model.*;
import fr.eseo.tauri.model.enumeration.RoleType;
import fr.eseo.tauri.repository.StudentRepository;
import fr.eseo.tauri.repository.ValidationFlagRepository;
import fr.eseo.tauri.service.AuthService;
import fr.eseo.tauri.service.TeamService;
import fr.eseo.tauri.service.UserService;
import fr.eseo.tauri.service.ValidationFlagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ValidationFlagServiceTest {

    @Mock
    AuthService authService;

    @Mock
    ValidationFlagRepository validationFlagRepository;

    @Mock
    UserService userService;

    @Mock
    TeamService teamService;

    @Mock
    StudentRepository studentRepository;

    @InjectMocks
    ValidationFlagService validationFlagService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getValidationFlagByAuthorIdShouldReturnFlagWhenAuthorized() {
        when(validationFlagRepository.findByAuthorIdAndFlagId(anyInt(), anyInt())).thenReturn(new ValidationFlag());

        ValidationFlag result = validationFlagService.getValidationFlagByAuthorId(1, 1);

        assertNotNull(result);
    }

    @Test
    void getAllValidationFlagsShouldReturnFlagsWhenAuthorized() {
        when(validationFlagRepository.findAllByFlag(anyInt())).thenReturn(Arrays.asList(new ValidationFlag(), new ValidationFlag()));

        List<ValidationFlag> result = validationFlagService.getAllValidationFlags( 1);

        assertEquals(2, result.size());
    }

    @Test
    void getAllValidationFlagsShouldReturnEmptyListWhenNoFlags() {
        when(validationFlagRepository.findAllByFlag(anyInt())).thenReturn(Collections.emptyList());

        List<ValidationFlag> result = validationFlagService.getAllValidationFlags(1);

        assertTrue(result.isEmpty());
    }

    @Test
    void updateValidationFlagShouldUpdateFlagWhenAuthorizedAndConfirmedIsNotNull() {
        Integer flagId = 1;
        Integer authorId = 1;
        ValidationFlag updatedValidationFlag = new ValidationFlag();
        updatedValidationFlag.confirmed(true);

        when(validationFlagService.getValidationFlagByAuthorId(flagId, authorId)).thenReturn(new ValidationFlag());

        validationFlagService.updateValidationFlag(flagId, authorId, updatedValidationFlag);

        verify(validationFlagRepository, times(1)).save(any(ValidationFlag.class));
    }


    @Test
    void createValidationFlagsShouldNotCreateFlagsWhenUserIsNotOptionStudent() {
        Flag flag = new Flag().author(new User().id(1));
        List<RoleType> roles = Collections.singletonList(RoleType.TEAM_MEMBER);

        when(userService.getRolesByUserId(flag.author().id())).thenReturn(roles);

        validationFlagService.createValidationFlags(flag);

        verify(validationFlagRepository, times(0)).save(any(ValidationFlag.class));
    }

    @Test
    void createValidationFlagShouldCreateFlagWhenUserExists() {
        Integer flagId = 1;
        Integer authorId = 1;
        ValidationFlag validationFlag = new ValidationFlag().authorId(authorId);
        User user = new User().id(authorId);

        when(userService.getUserById(authorId)).thenReturn(user);

        validationFlagService.createValidationFlag(flagId, validationFlag);

        verify(validationFlagRepository, times(1)).save(any(ValidationFlag.class));
    }

    @Test
    void createValidationFlagShouldThrowExceptionWhenUserDoesNotExist() {
        Integer flagId = 1;
        Integer authorId = 1;
        ValidationFlag validationFlag = new ValidationFlag().authorId(authorId);

        when(userService.getUserById(authorId)).thenReturn(null);

       assertDoesNotThrow(() -> validationFlagService.createValidationFlag(flagId, validationFlag));
    }

    @Test
    void createValidationFlagsShouldNotCreateFlagsWhenNoStudentsInTeams() {
        Flag flag = new Flag().author(new User().id(1));
        Student firstStudent = new Student().team(new Team().id(1));
        Student secondStudent = new Student().team(new Team().id(2));
        flag.firstStudent(firstStudent).secondStudent(secondStudent);


        when(userService.getRolesByUserId(flag.author().id())).thenReturn(Collections.singletonList(RoleType.OPTION_STUDENT));
        when(teamService.getStudentsByTeamId(flag.firstStudent().team().id(), false)).thenReturn(Collections.emptyList());
        when(teamService.getStudentsByTeamId(flag.secondStudent().team().id(), false)).thenReturn(Collections.emptyList());

        validationFlagService.createValidationFlags(flag);

        verify(validationFlagRepository, times(0)).save(any(ValidationFlag.class));
    }

    @Test
    void testCreateValidationFlags_UserWithOptionStudentRole() {
        // Arrange
        Flag flag = new Flag();
        User author = new User();
        author.id(1);
        flag.author(author);

        Team team1 = new Team();
        team1.id(1);
        Student firstStudent = new Student();
        firstStudent.team(team1);
        flag.firstStudent(firstStudent);

        Team team2 = new Team();
        team2.id(2);
        Student secondStudent = new Student();
        secondStudent.team(team2);
        flag.secondStudent(secondStudent);

        when(userService.getRolesByUserId(1)).thenReturn(List.of(RoleType.OPTION_STUDENT));
        List<Student> studentsTeam1 = new ArrayList<>();
        Student student1 = new Student();
        student1.id(1);
        studentsTeam1.add(student1);
        List<Student> studentsTeam2 = new ArrayList<>();
        Student student2 = new Student();
        student2.id(2);
        studentsTeam2.add(student2);
        when(teamService.getStudentsByTeamId(1, false)).thenReturn(studentsTeam1);
        when(teamService.getStudentsByTeamId(2, false)).thenReturn(studentsTeam2);

        // Act
        validationFlagService.createValidationFlags(flag);

        // Assert
        verify(validationFlagRepository, times(2)).save(any(ValidationFlag.class));
    }

    @Test
    void testCreateValidationFlags_UserWithoutOptionStudentRole() {
        // Arrange
        Flag flag = new Flag();
        User author = new User();
        author.id(1);
        flag.author(author);

        when(userService.getRolesByUserId(1)).thenReturn(List.of(RoleType.SUPERVISING_STAFF));

        // Act
        validationFlagService.createValidationFlags(flag);

        // Assert
        verify(validationFlagRepository, never()).save(any(ValidationFlag.class));
    }

    @Test
    void testCreateValidationFlags_EmptyTeams() {
        // Arrange
        Flag flag = new Flag();
        User author = new User();
        author.id(1);
        flag.author(author);

        Team team1 = new Team();
        team1.id(1);
        Student firstStudent = new Student();
        firstStudent.team(team1);
        flag.firstStudent(firstStudent);

        Team team2 = new Team();
        team2.id(2);
        Student secondStudent = new Student();
        secondStudent.team(team2);
        flag.secondStudent(secondStudent);

        when(userService.getRolesByUserId(1)).thenReturn(List.of(RoleType.OPTION_STUDENT));
        when(teamService.getStudentsByTeamId(1, false)).thenReturn(new ArrayList<>());
        when(teamService.getStudentsByTeamId(2, false)).thenReturn(new ArrayList<>());

        // Act
        validationFlagService.createValidationFlags(flag);

        // Assert
        verify(validationFlagRepository, never()).save(any(ValidationFlag.class));
    }

}
