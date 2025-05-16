package fr.eseo.tauri.unit.service;

import fr.eseo.tauri.exception.ResourceNotFoundException;
import fr.eseo.tauri.model.Flag;
import fr.eseo.tauri.model.Project;
import fr.eseo.tauri.model.Student;
import fr.eseo.tauri.model.User;
import fr.eseo.tauri.model.enumeration.FlagType;
import fr.eseo.tauri.repository.FlagRepository;
import fr.eseo.tauri.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@Nested
class FlagServiceTest {

    @InjectMocks
    private FlagService flagService;

    @Mock
    private FlagRepository flagRepository;

    @Mock
    private UserService userService;

    @Mock
    private ProjectService projectService;

    @Mock
    private ValidationFlagService validationFlagService;

    @Mock
    private StudentService studentService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getFlagByIdShouldReturnFlagWhenAuthorized() {
        Flag flag = new Flag();

        when(flagRepository.findById(anyInt())).thenReturn(java.util.Optional.of(flag));

        Flag result = flagService.getFlagById(1);

        assertEquals(flag, result);
    }

    @Test
    void getAllFlagsByProjectShouldReturnFlagsWhenAuthorized() {
        List<Flag> flags = Collections.singletonList(new Flag());

        when(flagRepository.findAllByProject(anyInt())).thenReturn(flags);

        List<Flag> result = flagService.getAllFlagsByProject(1);

        assertEquals(flags, result);
    }

    @Test
    void updateFlagShouldUpdateFlagWhenAuthorized() {
        Flag flag = new Flag();
        Flag updatedFlag = new Flag();
        updatedFlag.description("updated description");

        when(flagRepository.findById(anyInt())).thenReturn(java.util.Optional.of(flag));

        flagService.updateFlag( 1, updatedFlag);

        verify(flagRepository, times(1)).save(any(Flag.class));
    }

    @Test
    void updateFlagShouldThrowExceptionWhenFlagNotFound() {
        Flag updatedFlag = new Flag();

        when(flagRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> flagService.updateFlag(1, updatedFlag));
    }

    @Test
    void deleteFlagShouldDeleteFlagWhenAuthorized() {
        Flag flag = new Flag();

        when(flagRepository.findById(anyInt())).thenReturn(Optional.of(flag));

        flagService.deleteFlag(1);

        verify(flagRepository, times(1)).deleteById(anyInt());
    }

    @Test
    void deleteFlagShouldThrowExceptionWhenFlagNotFound() {
        when(flagRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> flagService.deleteFlag(1));
    }

    @Test
    void deleteAllFlagsByProjectShouldDeleteFlagsWhenAuthorized() {
        flagService.deleteAllFlagsByProject(1);

        verify(flagRepository, times(1)).deleteAllByProject(anyInt());
    }

    @Test
    void getFlagsByAuthorAndTypeShouldReturnFlagsWhenAuthorized() {
        Integer authorId = 1;
        FlagType type = FlagType.REPORTING;

        when(flagRepository.findByAuthorIdAndType(authorId, type)).thenReturn(Collections.singletonList(new Flag()));

        List<Flag> result = flagService.getFlagsByAuthorAndType(authorId, type);

        assertFalse(result.isEmpty());
    }

    @Test
    void getFlagsByAuthorAndTypeShouldReturnEmptyListWhenNoFlagsFound() {
        Integer authorId = 1;
        FlagType type = FlagType.REPORTING;

        when(flagRepository.findByAuthorIdAndType(authorId, type)).thenReturn(Collections.emptyList());

        List<Flag> result = flagService.getFlagsByAuthorAndType(authorId, type);

        assertTrue(result.isEmpty());
    }

    @Test
    void createFlagShouldCreateFlagWhenAllFieldsAreValid() {
        Flag flag = new Flag();
        flag.authorId(1);
        flag.projectId(1);
        flag.firstStudentId(1);
        flag.secondStudentId(1);

        when(userService.getUserById(any())).thenReturn(new User());
        when(projectService.getProjectById(any())).thenReturn(new Project());
        when(studentService.getStudentById(any())).thenReturn(new Student());

        flagService.createFlag(flag);

        verify(flagRepository, times(1)).save(any(Flag.class));
        verify(validationFlagService, times(1)).createValidationFlags(any(Flag.class));
    }

    @Test
    void createFlagShouldCreateFlagWhenOnlyMandatoryFieldsAreValid() {
        Flag flag = new Flag();
        flag.authorId(1);
        flag.projectId(1);

        when(userService.getUserById(any())).thenReturn(new User());
        when(projectService.getProjectById(any())).thenReturn(new Project());

        flagService.createFlag(flag);

        verify(flagRepository, times(1)).save(any(Flag.class));
        verify(validationFlagService, times(1)).createValidationFlags(any(Flag.class));
    }

    @Test
    void getFlagsByConcernedTeamIdShouldReturnFlagsWhenTeamIdExists() {
        Integer teamId = 1;
        List<Flag> flags = Collections.singletonList(new Flag());

        when(flagRepository.findByConcernedTeamId(teamId)).thenReturn(flags);

        List<Flag> result = flagService.getFlagsByConcernedTeamId(teamId);

        assertEquals(flags, result);
    }

    @Test
    void getFlagsByConcernedTeamIdShouldReturnEmptyListWhenNoFlagsFound() {
        Integer teamId = 1;

        when(flagRepository.findByConcernedTeamId(teamId)).thenReturn(Collections.emptyList());

        List<Flag> result = flagService.getFlagsByConcernedTeamId(teamId);

        assertTrue(result.isEmpty());
    }

    @Test
    void updateFlagShouldUpdateAllFieldsWhenAllFieldsAreNonNull() {
        Integer id = 1;
        Flag updatedFlag = new Flag();
        updatedFlag.description("New Description");
        updatedFlag.type(FlagType.REPORTING);
        updatedFlag.status(true);
        updatedFlag.firstStudentId(1);
        updatedFlag.secondStudentId(2);
        updatedFlag.authorId(3);
        updatedFlag.projectId(4);

        Flag existingFlag = new Flag();
        existingFlag.description("Old Description");
        existingFlag.type(FlagType.VALIDATION);
        existingFlag.status(true);
        existingFlag.firstStudent(new Student());
        existingFlag.secondStudent(new Student());
        existingFlag.author(new User());
        existingFlag.project(new Project());

        when(flagRepository.findById(id)).thenReturn(Optional.of(existingFlag));
        when(studentService.getStudentById(updatedFlag.firstStudentId())).thenReturn(new Student());
        when(studentService.getStudentById(updatedFlag.secondStudentId())).thenReturn(new Student());
        when(userService.getUserById(updatedFlag.authorId())).thenReturn(new User());
        when(projectService.getProjectById(updatedFlag.projectId())).thenReturn(new Project());

        flagService.updateFlag(id, updatedFlag);

        assertEquals("New Description", existingFlag.description());
        assertNotNull(existingFlag.firstStudent());
        assertNotNull(existingFlag.secondStudent());
        assertNotNull(existingFlag.author());
        assertNotNull(existingFlag.project());
        verify(flagRepository, times(1)).save(existingFlag);
    }
}
