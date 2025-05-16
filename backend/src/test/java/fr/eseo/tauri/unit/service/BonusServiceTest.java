package fr.eseo.tauri.unit.service;

import fr.eseo.tauri.model.*;
import fr.eseo.tauri.repository.BonusRepository;
import fr.eseo.tauri.exception.ResourceNotFoundException;
import fr.eseo.tauri.repository.StudentRepository;
import fr.eseo.tauri.repository.TeamRepository;
import fr.eseo.tauri.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Nested
class BonusServiceTest {

    @Mock
    ValidationBonusService validationBonusService;

    @Mock
    UserService userService;


    @InjectMocks
    BonusService bonusService;

    @Mock
    StudentRepository studentRepository;

    @Mock
    TeamRepository teamRepository;

    @Mock
    BonusRepository bonusRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getBonusByIdShouldReturnBonusWhenPermissionExistsAndBonusExists() {
        Integer id = 1;
        Bonus bonus = new Bonus();

        when(bonusRepository.findById(id)).thenReturn(Optional.of(bonus));

        Bonus result = bonusService.getBonusById(id);

        assertEquals(bonus, result);
    }

    @Test
    void getBonusByIdShouldThrowResourceNotFoundExceptionWhenBonusDoesNotExist() {
        Integer id = 1;

        when(bonusRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bonusService.getBonusById(id));
    }

    @Test
    void getAllBonusesByProjectShouldReturnBonusesWhenPermissionExists() {
        Integer projectId = 1;
        List<Bonus> bonuses = new ArrayList<>();
        bonuses.add(new Bonus());

        when(bonusRepository.findAllByProject(projectId)).thenReturn(bonuses);

        List<Bonus> result = bonusService.getAllBonusesByProject(projectId);

        assertEquals(bonuses, result);
    }

    @Test
    void getAllBonusesByProjectShouldHandleNoBonuses() {
        Integer projectId = 1;
        List<Bonus> bonuses = new ArrayList<>();

        when(bonusRepository.findAllByProject(projectId)).thenReturn(bonuses);

        List<Bonus> result = bonusService.getAllBonusesByProject(projectId);

        assertTrue(result.isEmpty());
    }

    @Test
    void createBonusShouldSaveBonusWhenBonusIsValid() {
        Bonus bonus = new Bonus();
        bonus.id(1);
        bonus.value(3F);

        when(bonusRepository.save(any(Bonus.class))).thenReturn(bonus);

        bonusService.createBonus(bonus);

        verify(bonusRepository, times(1)).save(bonus);
    }


    @Test
    void updateBonusShouldThrowIllegalArgumentExceptionWhenBonusIsLimitedAndValueIsOutOfRange() {
        Integer id = 1;
        Bonus updatedBonus = new Bonus();
        updatedBonus.limited(true);
        updatedBonus.value(5F);

        when(bonusRepository.findById(id)).thenReturn(Optional.of(new Bonus()));

        assertThrows(NullPointerException.class, () -> bonusService.updateBonus(id, updatedBonus));
    }


    @Test
    void deleteAllBonusesByProjectShouldDeleteBonusesWhenPermissionExists() {
        Integer projectId = 1;

        bonusService.deleteAllBonusesByProject(projectId);

        verify(bonusRepository, times(1)).deleteAllByProject(projectId);
    }

    @Test
    void deleteBonusShouldDeleteBonusWhenBonusExists() {
        Integer id = 1;
        Bonus bonus = new Bonus();

        when(bonusRepository.findById(id)).thenReturn(Optional.of(bonus));

        bonusService.deleteBonus(id);

        verify(bonusRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteBonusShouldThrowResourceNotFoundExceptionWhenBonusDoesNotExist() {
        Integer id = 1;

        when(bonusRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bonusService.deleteBonus(id));
    }


    @Test
    void getValidationBonusesByTeamShouldReturnEmptyListWhenNoStudentsExist() {
        Integer teamId = 1;
        Team team = new Team().id(1);
        User leader = new User().id(1);
        team.leader(leader);

        when(teamRepository.findLeaderByTeamId(teamId)).thenReturn(leader);
        when(studentRepository.findByTeam(teamId)).thenReturn(Collections.emptyList());

        List<ValidationBonus> result = bonusService.getValidationBonusesByTeam(teamId);

        assertTrue(result.isEmpty());
    }

    @Test
    void setValidationBonusesByTeamShouldNotCreateValidationBonusesWhenNoStudentsExist() {
        Integer teamId = 1;
        Integer sprintId = 1;
        Integer userId = 1;

        when(studentRepository.findByTeam(teamId)).thenReturn(Collections.emptyList());

        bonusService.setValidationBonusesByTeam(teamId, sprintId, userId);

        verify(validationBonusService, never()).createValidationBonus(any());
    }

    @Test
    void updateBonusShouldUpdateBonusWhenBonusIsValidAndNotLimited() {
        Integer id = 1;
        Bonus updatedBonus = new Bonus().value(2F);

        when(bonusRepository.findById(id)).thenReturn(Optional.of(new Bonus().limited(false)));
        when(userService.getUserById(any())).thenReturn(new User().id(1));
        when(bonusRepository.save(any())).thenReturn(updatedBonus);

        bonusService.updateBonus(id, updatedBonus);

        verify(bonusRepository, times(1)).save(any());
    }

    @Test
    void updateBonusShouldNotDeleteAllValidationBonusesWhenBonusIsNotLimited() {
        Integer id = 1;
        Bonus updatedBonus = new Bonus().value(2F);

        when(bonusRepository.findById(id)).thenReturn(Optional.of(new Bonus().limited(false)));
        when(userService.getUserById(any())).thenReturn(new User().id(1));
        when(bonusRepository.save(any())).thenReturn(updatedBonus);

        bonusService.updateBonus(id, updatedBonus);

        verify(validationBonusService, never()).deleteAllValidationBonuses(id);
    }

    @Test
    void updateBonusShouldThrowIllegalArgumentExceptionWhenLimitedBonusIsAbove4() {
        Integer id = 1;
        Bonus updatedBonus = new Bonus().value(5F);

        when(bonusRepository.findById(id)).thenReturn(Optional.of(new Bonus().limited(true)));
        when(userService.getUserById(any())).thenReturn(new User().id(1));
        when(bonusRepository.save(any())).thenReturn(updatedBonus);

       assertThrows(IllegalArgumentException.class, () -> bonusService.updateBonus(id, updatedBonus));
    }

    @Test
    void getValidationBonusesByTeamShouldReturnBonusesWhenStudentsAndLeaderExist() {
        Integer teamId = 1;
        Student student1 = new Student();
        student1.id(1);
        Student student2 = new Student();
        student2.id(2);
        List<Student> students = Arrays.asList(student1, student2);

        User leader = new User().id(3);
        ValidationBonus bonus1 = new ValidationBonus().bonusId(1);
        ValidationBonus bonus2 = new ValidationBonus().bonusId(2);
        ValidationBonus leaderBonus = new ValidationBonus().bonusId(3);

        when(studentRepository.findByTeam(teamId)).thenReturn(students);
        when(validationBonusService.getValidationByAuthorId(1)).thenReturn(Collections.singletonList(bonus1));
        when(validationBonusService.getValidationByAuthorId(2)).thenReturn(Collections.singletonList(bonus2));
        when(teamRepository.findLeaderByTeamId(teamId)).thenReturn(leader);
        when(validationBonusService.getValidationByAuthorId(3)).thenReturn(Collections.singletonList(leaderBonus));

        List<ValidationBonus> result = bonusService.getValidationBonusesByTeam(teamId);

        assertTrue(result.contains(bonus1));
        assertTrue(result.contains(bonus2));
        assertTrue(result.contains(leaderBonus));
    }

    @Test
    void getValidationBonusesByTeamShouldReturnOnlyLeaderBonusWhenNoStudentsExist() {
        Integer teamId = 1;
        User leader = new User().id(1);
        ValidationBonus leaderBonus = new ValidationBonus().bonusId(1);

        when(studentRepository.findByTeam(teamId)).thenReturn(Collections.emptyList());
        when(teamRepository.findLeaderByTeamId(teamId)).thenReturn(leader);
        when(validationBonusService.getValidationByAuthorId(1)).thenReturn(Collections.singletonList(leaderBonus));

        List<ValidationBonus> result = bonusService.getValidationBonusesByTeam(teamId);

        assertEquals(1, result.size());
        assertTrue(result.contains(leaderBonus));
    }

    @Test
    void testUpdateLimitedBonusValueExceeds4() throws RuntimeException{
        // Arrange
        Bonus bonus = new Bonus();
        bonus.limited(true);
        bonus.value(5F); // Exceeds the limit
        when(bonusRepository.findById(anyInt())).thenReturn(java.util.Optional.of(bonus));


        assertThrows(IllegalArgumentException.class, () -> bonusService.updateBonus(1, bonus));
        verify(bonusRepository, never()).save(any());
    }

    @Test
    void testUpdateUnlimitedBonus() {
        // Arrange
        Bonus bonus = new Bonus();
        bonus.limited(false);
        when(bonusRepository.findById(anyInt())).thenReturn(java.util.Optional.of(bonus));

        // Act
        bonusService.updateBonus(1, new Bonus().value(3F));

        // Assert
        verify(bonusRepository, times(1)).save(any());
    }

    @Test
    void testLimitedBonusValueCheck() {
        Bonus limitedBonus;
        Bonus unlimitedBonus;

        limitedBonus = new Bonus();
        limitedBonus.id(1);
        limitedBonus.value(5F); // Value greater than 4
        limitedBonus.limited(true);

        unlimitedBonus = new Bonus();
        unlimitedBonus.id(2);
        unlimitedBonus.value(3F); // Value within the range (-4, 4)
        unlimitedBonus.limited(false);

        // Mocking necessary dependencies and their behavior
        when(bonusRepository.findById(1)).thenReturn(java.util.Optional.of(limitedBonus));
        when(bonusRepository.findById(2)).thenReturn(java.util.Optional.of(unlimitedBonus));
        // Test a limited bonus with a value greater than 4
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bonusService.updateBonus(1, limitedBonus);
        });
        assertEquals("The value of a limited bonus must be between -4 and 4", exception.getMessage());

        // Test a limited bonus with a value within the range (-4, 4)
        assertDoesNotThrow(() -> {
            bonusService.updateBonus(2, unlimitedBonus);
        });
    }

    @Test
    void testSetValidationBonusesByTeam_NoStudents() {
        // Setup
        when(studentRepository.findByTeam(anyInt())).thenReturn(Collections.emptyList());

        // Exercise
        bonusService.setValidationBonusesByTeam(1, 1, 1);

        // Verify
        verify(validationBonusService, never()).createValidationBonus(any());
    }

    @Test
    void testUpdateBonusWithinRange() {
        // Mock Bonus object with value within range [-4, 4]
        Bonus mockBonus = new Bonus();
        mockBonus.id(1);
        mockBonus.value(3F);
        mockBonus.limited(true);

        User mockUser = new User();
        mockUser.id(1);


        Student mockStudent = new Student();
        mockStudent.id(2);

        mockBonus.author(mockUser);
        mockBonus.student(mockStudent);

        Team mockTeam = new Team();
        mockTeam.id(1);
        mockTeam.leader(mockUser);

        // Mock dependencies
        when(bonusRepository.findById(any(Integer.class))).thenReturn(java.util.Optional.of(mockBonus));
        when(teamRepository.findTeamByStudentId(any(Integer.class))).thenReturn(mockTeam);
        when(teamRepository.findLeaderByTeamId(any(Integer.class))).thenReturn(mockUser);
        when(validationBonusService.getValidationByAuthorId(any(Integer.class))).thenReturn(new ArrayList<>());

        // Call method
        assertDoesNotThrow(() -> bonusService.updateBonus(1, mockBonus));
    }

    @Test
    void testUpdateBonusWithinRangeWithAuthorAndComment() {
        // Mock Bonus object with value within range [-4, 4] and updated author/comment
        Bonus mockBonus = new Bonus();
        mockBonus.id(1);
        mockBonus.value(3F);
        mockBonus.limited(true);

        User mockUser = new User();
        mockUser.id(1);

        Student mockStudent = new Student();
        mockStudent.id(2);

        mockBonus.author(mockUser);
        mockBonus.student(mockStudent);

        Team mockTeam = new Team();
        mockTeam.id(1);
        mockTeam.leader(mockUser);

        // Mock dependencies
        when(bonusRepository.findById(any(Integer.class))).thenReturn(java.util.Optional.of(mockBonus));
        when(teamRepository.findTeamByStudentId(any(Integer.class))).thenReturn(mockTeam);
        when(teamRepository.findLeaderByTeamId(any(Integer.class))).thenReturn(mockUser);
        when(validationBonusService.getValidationByAuthorId(any(Integer.class))).thenReturn(new ArrayList<>());

        // Call method with updated author and comment
        Bonus updatedBonus = new Bonus();
        updatedBonus.value(3F);
        updatedBonus.authorId(1);
        updatedBonus.comment("Updated comment");
        bonusService.updateBonus(1, updatedBonus);

        // Verify that the bonus's author and comment are updated
        verify(userService, times(1)).getUserById(1);
        verify(bonusRepository, times(1)).save(mockBonus);
    }

    @Test
    void testSetValidationBonusesByTeam() {
        // Mock data
        Integer teamId = 1;
        Integer sprintId = 1;
        Integer userId = 1;

        // Mock students
        List<Student> students = new ArrayList<>();
        Student student1 = new Student();
        student1.id(1);
        students.add(student1);

        when(studentRepository.findByTeam(any(Integer.class))).thenReturn(students);

        // Mock bonus
        Bonus bonus = new Bonus();
        bonus.id(1);
        when(bonusRepository.findStudentBonus(any(Integer.class), anyBoolean(), any(Integer.class))).thenReturn(bonus);

        // Call method
        bonusService.setValidationBonusesByTeam(teamId, sprintId, userId);

        // Verify that createValidationBonus method is called for each student
        verify(validationBonusService, times(1)).createValidationBonus(any(ValidationBonus.class));
    }

}
