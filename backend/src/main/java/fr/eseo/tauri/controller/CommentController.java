package fr.eseo.tauri.controller;

import fr.eseo.tauri.model.Comment;
import fr.eseo.tauri.service.CommentService;
import fr.eseo.tauri.util.CustomLogger;
import fr.eseo.tauri.util.ResponseMessage;
import fr.eseo.tauri.util.valid.Create;
import fr.eseo.tauri.util.valid.Update;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
@Tag(name = "comments")
public class CommentController {

    private final CommentService commentService;
    private final ResponseMessage responseMessage = new ResponseMessage("comment");

    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Integer id) {
        Comment comment = commentService.getCommentById(id);
        return ResponseEntity.ok(comment);
    }

    @GetMapping
    public ResponseEntity<List<Comment>> getAllCommentsByProject(@RequestParam("projectId") Integer projectId) {
        List<Comment> comments = commentService.getAllCommentsByProject(projectId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping
    public ResponseEntity<String> createComment(@Validated(Create.class) @RequestBody Comment comment) {
        commentService.createComment(comment);
        CustomLogger.info(responseMessage.create());
        return ResponseEntity.ok(responseMessage.create());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> updateComment(@PathVariable Integer id, @Validated(Update.class)@RequestBody Comment updatedComment) {
        commentService.updateComment(id, updatedComment);
        CustomLogger.info(responseMessage.update());
        return ResponseEntity.ok(responseMessage.update());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable Integer id) {
        commentService.deleteComment(id);
        CustomLogger.info(responseMessage.delete());
        return ResponseEntity.ok(responseMessage.delete());
    }

    @DeleteMapping
    public ResponseEntity<String> deleteAllCommentsByProject(@RequestParam("projectId") Integer projectId) {
        commentService.deleteAllCommentsByProject(projectId);
        CustomLogger.info(responseMessage.deleteAllFromCurrentProject());
        return ResponseEntity.ok(responseMessage.deleteAllFromCurrentProject());
    }
}