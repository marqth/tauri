package fr.eseo.tauri.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import fr.eseo.tauri.model.Comment;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @Query("SELECT c FROM Comment c WHERE c.sprint.project.id = :projectId")
    List<Comment> findAllByProject(Integer projectId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM comments WHERE sprint_id IN (SELECT id FROM sprints WHERE project_id = :projectId)", nativeQuery = true)
    void deleteAllByProject(Integer projectId);

    @Query("SELECT c FROM Comment c WHERE c.team.id = :teamId AND c.sprint.id = :sprintId")
    List<Comment> findAllByTeamIdAndSprintId(Integer teamId, Integer sprintId);

    @Query("SELECT c FROM Comment c WHERE c.student.id = :studentId AND c.sprint.id = :sprintId")
    List<Comment> findAllByStudentIdAndSprintId(Integer studentId, Integer sprintId);

    @Query("SELECT c FROM Comment c WHERE c.student.id = :studentId AND c.sprint.id = :sprintId AND c.author.id = :authorId")
    List<Comment> findAllByTeamAndSprintAndAuthor(Integer studentId, Integer sprintId, Integer authorId);

    @Query("SELECT c FROM Comment c WHERE c.student.team.id = :teamId AND c.sprint.id = :sprintId")
    List<Comment> findIndividualCommentsByTeamIdAndSprintId(Integer teamId, Integer sprintId);
}
