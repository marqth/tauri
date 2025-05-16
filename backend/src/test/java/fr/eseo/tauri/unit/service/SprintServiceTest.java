package fr.eseo.tauri.unit.service;

import fr.eseo.tauri.exception.ResourceNotFoundException;
import fr.eseo.tauri.model.*;
import fr.eseo.tauri.model.enumeration.SprintEndType;
import fr.eseo.tauri.repository.CommentRepository;
import fr.eseo.tauri.repository.SprintRepository;
import fr.eseo.tauri.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Nested
class SprintServiceTest {

    @Mock
    private SprintRepository sprintRepository;

    @Mock
    private ProjectService projectService;

    @Mock
    private StudentService studentService;

    @Mock
    private PresentationOrderService presentationOrderService;

    @Mock
    private BonusService bonusService;

    @Mock
    private TeamService teamService;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private SprintService sprintService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getSprintByIdShouldReturnSprintWhenAuthorizedAndSprintExists() {
        Integer id = 1;
        Sprint sprint = new Sprint();

        when(sprintRepository.findById(id)).thenReturn(Optional.of(sprint));

        Sprint result = sprintService.getSprintById(id);

        assertEquals(sprint, result);
    }

    @Test
    void getSprintByIdShouldThrowResourceNotFoundExceptionWhenSprintDoesNotExist() {
        Integer id = 1;

        when(sprintRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> sprintService.getSprintById(id));
    }

    @Test
    void getAllSprintsByProjectShouldReturnSprintsWhenAuthorizedAndProjectExists() {
        Integer projectId = 1;
        List<Sprint> sprints = Arrays.asList(new Sprint(), new Sprint());

        when(sprintRepository.findAllByProject(projectId)).thenReturn(sprints);

        List<Sprint> result = sprintService.getAllSprintsByProject(projectId);

        assertEquals(sprints, result);
    }

    @Test
    void getAllSprintsByProjectShouldHandleNoSprints() {
        Integer projectId = 1;
        List<Sprint> sprints = Collections.emptyList();

        when(sprintRepository.findAllByProject(projectId)).thenReturn(sprints);

        List<Sprint> result = sprintService.getAllSprintsByProject(projectId);

        assertEquals(sprints, result);
    }

    @Test
    void deleteSprintShouldDeleteSprintWhenAuthorizedAndSprintExists() {
        Integer id = 1;
        Sprint sprint = new Sprint();

        when(sprintRepository.findById(id)).thenReturn(Optional.of(sprint));

        sprintService.deleteSprint(id);

        verify(sprintRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteSprintShouldThrowResourceNotFoundExceptionWhenSprintDoesNotExist() {
        Integer id = 1;

        when(sprintRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> sprintService.deleteSprint(id));
    }

    @Test
    void deleteAllSprintsByProjectShouldDeleteSprintsWhenAuthorizedAndProjectExists() {
        Integer projectId = 1;

        sprintService.deleteAllSprintsByProject(projectId);

        verify(sprintRepository, times(1)).deleteAllByProject(projectId);
    }

    @Test
    void deleteAllSprintsByProjectShouldHandleNoSprints() {
        Integer projectId = 1;

        doNothing().when(sprintRepository).deleteAllByProject(projectId);

        sprintService.deleteAllSprintsByProject(projectId);

        verify(sprintRepository, times(1)).deleteAllByProject(projectId);
    }

    @Test
    void createSprintShouldSaveSprintWhenProjectExistsAndStudentsAreEmpty() {
        int sprintId = 1;
        Sprint sprint = new Sprint();
        sprint.projectId(1);

        when(projectService.getProjectById(sprint.projectId())).thenReturn(new Project());
        when(studentService.getAllStudentsByProject(sprint.projectId())).thenReturn(Collections.emptyList());

        sprintService.createSprint(sprint, sprintId);

        verify(sprintRepository, times(1)).save(sprint);
    }

    @Test
    void createSprintTest() {
        int sprintId = 1;
        Sprint sprint = new Sprint();
        sprint.id(1);

        Project project = new Project();
        project.id(1);
        sprint.project(project);

        Student student1 = new Student();
        student1.id(1);
        Team team1 = new Team();
        team1.id(1);
        student1.team(team1);

        Student student2 = new Student();
        student2.id(2);
        Team team2 = new Team();
        team2.id(2);
        student2.team(team2);

        List<Student> students = List.of(student1, student2);
        List<Team> teams = List.of(team1, team2);

        when(projectService.getProjectById(sprint.projectId())).thenReturn(project);
        when(studentService.getAllStudentsByProject(sprint.projectId())).thenReturn(students);
        when(teamService.getAllTeamsByProject(sprint.projectId())).thenReturn(teams);

        sprintService.createSprint(sprint, sprintId);

        verify(sprintRepository, times(1)).save(sprint);
        verify(presentationOrderService, times(2)).createPresentationOrder(any(PresentationOrder.class));
        verify(bonusService, times(4)).createBonus(any(Bonus.class));
        verify(studentService, times(1)).getAllStudentsByProject(sprint.projectId());
        verify(teamService, times(1)).getAllTeamsByProject(sprint.projectId());
    }

    @Test
    void updateSprintShouldUpdateStartDateWhenProvided() {
        Integer id = 1;
        Sprint existingSprint = new Sprint();
        Sprint updatedSprint = new Sprint();
        updatedSprint.startDate(LocalDate.now());

        when(sprintRepository.findById(id)).thenReturn(Optional.of(existingSprint));

        sprintService.updateSprint(id, updatedSprint);

        assertEquals(updatedSprint.startDate(), existingSprint.startDate());
        verify(sprintRepository, times(1)).save(existingSprint);
    }

    @Test
    void updateSprintShouldUpdateEndDateWhenProvided() {
        Integer id = 1;
        Sprint existingSprint = new Sprint();
        Sprint updatedSprint = new Sprint();
        updatedSprint.endDate(LocalDate.now());

        when(sprintRepository.findById(id)).thenReturn(Optional.of(existingSprint));

        sprintService.updateSprint(id, updatedSprint);

        assertEquals(updatedSprint.endDate(), existingSprint.endDate());
        verify(sprintRepository, times(1)).save(existingSprint);
    }

    @Test
    void updateSprintShouldUpdateEndTypeWhenProvided() {
        Integer id = 1;
        Sprint existingSprint = new Sprint();
        Sprint updatedSprint = new Sprint();
        updatedSprint.endType(SprintEndType.NORMAL_SPRINT);

        when(sprintRepository.findById(id)).thenReturn(Optional.of(existingSprint));

        sprintService.updateSprint(id, updatedSprint);

        assertEquals(updatedSprint.endType(), existingSprint.endType());
        verify(sprintRepository, times(1)).save(existingSprint);
    }

    @Test
    void updateSprintShouldUpdateSprintOrderWhenProvided() {
        Integer id = 1;
        Sprint existingSprint = new Sprint();
        Sprint updatedSprint = new Sprint();
        updatedSprint.sprintOrder(2);

        when(sprintRepository.findById(id)).thenReturn(Optional.of(existingSprint));

        sprintService.updateSprint(id, updatedSprint);

        assertEquals(updatedSprint.sprintOrder(), existingSprint.sprintOrder());
        verify(sprintRepository, times(1)).save(existingSprint);
    }

    @Test
    void updateSprintShouldUpdateProjectWhenProjectIdIsProvided() {
        Integer id = 1;
        Sprint existingSprint = new Sprint();
        Sprint updatedSprint = new Sprint();
        updatedSprint.projectId(2);
        Project project = new Project();
        project.id(2);

        when(sprintRepository.findById(id)).thenReturn(Optional.of(existingSprint));
        when(projectService.getProjectById(updatedSprint.projectId())).thenReturn(project);

        sprintService.updateSprint(id, updatedSprint);

        assertEquals(project, existingSprint.project());
        verify(sprintRepository, times(1)).save(existingSprint);
    }

    @Test
    void updateSprintShouldThrowResourceNotFoundExceptionWhenSprintDoesNotExist() {
        Integer id = 1;
        Sprint updatedSprint = new Sprint();

        when(sprintRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> sprintService.updateSprint(id, updatedSprint));
    }

    @Test
    void getCurrentSprintShouldReturnCurrentSprintWhenSprintIsInProgress() {
        Integer projectId = 1;
        Sprint sprint1 = new Sprint();
        sprint1.startDate(LocalDate.now().minusDays(1));
        sprint1.endDate(LocalDate.now().plusDays(1));
        List<Sprint> sprints = Collections.singletonList(sprint1);

        when(sprintRepository.findAllByProject(projectId)).thenReturn(sprints);

        Sprint result = sprintService.getCurrentSprint(projectId);

        assertEquals(sprint1, result);
    }

    @Test
    void getCurrentSprintShouldReturnClosestSprintWhenNoSprintIsInProgress() {
        Integer projectId = 1;
        Sprint sprint1 = new Sprint();
        sprint1.startDate(LocalDate.now().plusDays(1));
        sprint1.endDate(LocalDate.now().plusDays(3));
        Sprint sprint2 = new Sprint();
        sprint2.startDate(LocalDate.now().plusDays(2));
        sprint2.endDate(LocalDate.now().plusDays(4));
        List<Sprint> sprints = Arrays.asList(sprint1, sprint2);

        when(sprintRepository.findAllByProject(projectId)).thenReturn(sprints);

        Sprint result = sprintService.getCurrentSprint(projectId);

        assertEquals(sprint1, result);
    }

    @Test
    void getCurrentSprintShouldReturnNullWhenNoSprintsExist() {
        Integer projectId = 1;
        List<Sprint> sprints = Collections.emptyList();

        when(sprintRepository.findAllByProject(projectId)).thenReturn(sprints);

        Sprint result = sprintService.getCurrentSprint(projectId);

        assertNull(result);
    }

    @Test
    void deleteSprintShouldDeleteSprintAndReorderWhenSprintOrderIsGreater() {
        Integer id = 1;
        Sprint deletedSprint = new Sprint();
        deletedSprint.sprintOrder(2);
        Sprint remainingSprint = new Sprint();
        remainingSprint.sprintOrder(3);
        List<Sprint> sprints = Collections.singletonList(remainingSprint);

        when(sprintRepository.findById(id)).thenReturn(Optional.of(deletedSprint));
        when(sprintRepository.findAllByProject(id)).thenReturn(sprints);

        sprintService.deleteSprint(id);

        verify(sprintRepository, times(1)).deleteById(id);
        assertEquals(2, remainingSprint.sprintOrder());
        verify(sprintRepository, times(1)).save(remainingSprint);
    }

    @Test
    void getTeamStudentsCommentsShouldReturnCommentsWhenTheyExist() {
        Integer sprintId = 1;
        Integer authorId = 1;
        Integer teamId = 1;
        Student student = new Student();
        student.id(1);
        Comment comment = new Comment();
        List<Comment> expectedComments = Collections.singletonList(comment);

        when(teamService.getStudentsByTeamId(teamId, true)).thenReturn(Collections.singletonList(student));
        when(commentRepository.findAllByTeamAndSprintAndAuthor(student.id(), sprintId, authorId)).thenReturn(expectedComments);

        List<Comment> result = sprintService.getTeamStudentsComments(sprintId, authorId, teamId);

        assertEquals(expectedComments, result);
    }

    @Test
    void getTeamStudentsCommentsShouldReturnEmptyListWhenNoStudentsInTeam() {
        Integer sprintId = 1;
        Integer authorId = 1;
        Integer teamId = 1;

        when(teamService.getStudentsByTeamId(teamId, true)).thenReturn(Collections.emptyList());

        List<Comment> result = sprintService.getTeamStudentsComments(sprintId, authorId, teamId);

        assertTrue(result.isEmpty());
    }

    @Test
    void getTeamStudentsCommentsShouldReturnEmptyListWhenNoCommentsExist() {
        Integer sprintId = 1;
        Integer authorId = 1;
        Integer teamId = 1;
        Student student = new Student();
        student.id(1);

        when(teamService.getStudentsByTeamId(teamId, true)).thenReturn(Collections.singletonList(student));
        when(commentRepository.findAllByTeamAndSprintAndAuthor(student.id(), sprintId, authorId)).thenReturn(Collections.emptyList());

        List<Comment> result = sprintService.getTeamStudentsComments(sprintId, authorId, teamId);

        assertTrue(result.isEmpty());
    }

}
