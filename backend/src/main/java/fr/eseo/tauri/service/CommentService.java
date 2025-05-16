package fr.eseo.tauri.service;

import fr.eseo.tauri.model.Comment;
import fr.eseo.tauri.exception.ResourceNotFoundException;
import fr.eseo.tauri.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;
    private final TeamService teamService;
    private final StudentService studentService;
    private final SprintService sprintService;

    public Comment getCommentById(Integer id) {
        return commentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("comment", id));
    }

    public List<Comment> getAllCommentsByProject(Integer projectId) {
        return commentRepository.findAllByProject(projectId);
    }

    public void createComment(Comment comment) {
        comment.author(userService.getUserById(comment.authorId()));
        comment.sprint(sprintService.getSprintById(comment.sprintId()));

        if(comment.teamId() == null) {
            comment.team(null);
        } else {
            comment.team(teamService.getTeamById(comment.teamId()));
        }

        if(comment.teamId() == null) {
            comment.team(null);
            comment.student(studentService.getStudentById(comment.studentId()));
        }

        if ((comment.team() == null) == (comment.student() == null)) {
            throw new IllegalArgumentException("Both team and student attributes cannot be either null or not null at the same time");
        }

        commentRepository.save(comment);
    }

    public void updateComment(Integer id, Comment updatedComment) {
        Comment comment = getCommentById(id);

        if (updatedComment.content() != null) comment.content(updatedComment.content());
        if (updatedComment.feedback() != null) comment.feedback(updatedComment.feedback());
        if (updatedComment.sprintId() != null) comment.sprint(sprintService.getSprintById(updatedComment.sprintId()));
        if (updatedComment.authorId() != null) comment.author(userService.getUserById(updatedComment.authorId()));
        if (updatedComment.teamId() != null) comment.team(teamService.getTeamById(updatedComment.teamId()));
        if (updatedComment.studentId() != null) comment.student(studentService.getStudentById(updatedComment.studentId()));

        commentRepository.save(comment);
    }

    public void deleteComment(Integer id) {
        getCommentById(id);
        commentRepository.deleteById(id);
    }

    public void deleteAllCommentsByProject(Integer projectId) {
        commentRepository.deleteAllByProject(projectId);
    }
}