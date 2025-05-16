package fr.eseo.tauri.unit.service;

import fr.eseo.tauri.exception.ResourceNotFoundException;
import fr.eseo.tauri.model.*;
import fr.eseo.tauri.repository.CommentRepository;
import fr.eseo.tauri.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    @Mock
    CommentRepository commentRepository;

    @Mock
    UserService userService;

    @Mock
    TeamService teamService;

    @Mock
    SprintService sprintService;

    @Mock
    StudentService studentService;

    @InjectMocks
    CommentService commentService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getCommentByIdShouldReturnCommentWhenPermissionExistsAndCommentExists() {
        Integer id = 1;
        Comment comment = new Comment();

        when(commentRepository.findById(id)).thenReturn(Optional.of(comment));

        Comment result = commentService.getCommentById(id);

        assertEquals(comment, result);
    }

    @Test
    void getCommentByIdShouldThrowResourceNotFoundExceptionWhenCommentDoesNotExist() {
        Integer id = 1;

        when(commentRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.getCommentById(id));
    }

    @Test
    void getAllCommentsByProjectShouldReturnCommentsWhenPermissionExists() {
        Integer projectId = 1;
        List<Comment> comments = Arrays.asList(new Comment(), new Comment());

        when(commentRepository.findAllByProject(projectId)).thenReturn(comments);

        List<Comment> result = commentService.getAllCommentsByProject(projectId);

        assertEquals(comments, result);
    }

    @Test
    void getAllCommentsByProjectShouldReturnEmptyListWhenNoCommentsExist() {
        Integer projectId = 1;

        when(commentRepository.findAllByProject(projectId)).thenReturn(Collections.emptyList());

        List<Comment> result = commentService.getAllCommentsByProject(projectId);

        assertTrue(result.isEmpty());
    }

    @Test
    void createCommentShouldSaveCommentWhenPermissionExistsAndCommentIsValid() {
        Comment comment = new Comment();
        comment.authorId(1);
        comment.sprintId(1);
        comment.teamId(1);

        when(userService.getUserById(comment.authorId())).thenReturn(new User());
        when(sprintService.getSprintById(comment.sprintId())).thenReturn(new Sprint());
        when(teamService.getTeamById(comment.teamId())).thenReturn(new Team());

        commentService.createComment(comment);

        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void deleteCommentShouldDeleteCommentWhenPermissionExistsAndCommentExists() {
        Integer id = 1;
        Comment comment = new Comment();

        when(commentRepository.findById(id)).thenReturn(Optional.of(comment));

        commentService.deleteComment(id);

        verify(commentRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteAllCommentsByProjectShouldDeleteCommentsWhenPermissionExists() {
        Integer projectId = 1;

        commentService.deleteAllCommentsByProject(projectId);

        verify(commentRepository, times(1)).deleteAllByProject(projectId);
    }

    @Test
    void updateCommentShouldNotUpdateCommentWhenUpdatedCommentFieldsAreNull() {
        Integer id = 1;
        Comment updatedComment = new Comment();
        Comment existingComment = new Comment();
        existingComment.content("Existing Content");
        existingComment.feedback(false);

        when(commentRepository.findById(id)).thenReturn(Optional.of(existingComment));
        commentService.updateComment(id, updatedComment);

        assertEquals("Existing Content", existingComment.content());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void updateCommentShouldUpdateAllFieldsWhenAllFieldsAreNonNull() {
        Integer id = 1;
        Comment updatedComment = new Comment();
        updatedComment.content("New Content");
        updatedComment.feedback(true);
        updatedComment.sprintId(1);
        updatedComment.authorId(1);
        updatedComment.teamId(1);

        Comment existingComment = new Comment();
        existingComment.content("Old Content");
        existingComment.feedback(false);
        existingComment.sprint(new Sprint());
        existingComment.author(new User());
        existingComment.team(new Team());

        when(commentRepository.findById(id)).thenReturn(Optional.of(existingComment));
        when(sprintService.getSprintById(updatedComment.sprintId())).thenReturn(new Sprint());
        when(userService.getUserById(updatedComment.authorId())).thenReturn(new User());
        when(teamService.getTeamById(updatedComment.teamId())).thenReturn(new Team());

        commentService.updateComment(id, updatedComment);

        assertEquals("New Content", existingComment.content());
        assertTrue(existingComment.feedback());
        verify(sprintService, times(1)).getSprintById(updatedComment.sprintId());
        verify(userService, times(1)).getUserById(updatedComment.authorId());
        verify(teamService, times(1)).getTeamById(updatedComment.teamId());
        verify(commentRepository, times(1)).save(existingComment);
    }

    @Test
    void testCreateComment_TeamIdNull_StudentIdNotNull() {
        Comment comment = new Comment();
        comment.authorId(1);
        comment.sprintId(1);
        comment.studentId(1);
        comment.teamId(null);

        when(userService.getUserById(1)).thenReturn(new User());
        when(sprintService.getSprintById(1)).thenReturn(new Sprint());
        when(studentService.getStudentById(1)).thenReturn(new Student());

        commentService.createComment(comment);

        assertNull(comment.team());
        assertNotNull(comment.student());
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void testCreateComment_TeamIdNotNull_StudentIdNull() {
        Comment comment = new Comment();
        comment.authorId(1);
        comment.sprintId(1);
        comment.studentId(null);
        comment.teamId(1);

        when(userService.getUserById(1)).thenReturn(new User());
        when(sprintService.getSprintById(1)).thenReturn(new Sprint());
        when(teamService.getTeamById(1)).thenReturn(new Team());

        commentService.createComment(comment);

        assertNotNull(comment.team());
        assertNull(comment.student());
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void testCreateComment_BothTeamIdAndStudentIdNull() {
        Comment comment = new Comment();
        comment.authorId(1);
        comment.sprintId(1);
        comment.studentId(null);
        comment.teamId(null);

        when(userService.getUserById(1)).thenReturn(new User());
        when(sprintService.getSprintById(1)).thenReturn(new Sprint());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            commentService.createComment(comment);
        });

        assertEquals("Both team and student attributes cannot be either null or not null at the same time", exception.getMessage());
        verify(commentRepository, never()).save(any(Comment.class));
    }


}


