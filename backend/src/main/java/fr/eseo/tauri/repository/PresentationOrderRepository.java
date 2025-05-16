package fr.eseo.tauri.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import fr.eseo.tauri.model.PresentationOrder;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PresentationOrderRepository extends JpaRepository<PresentationOrder, Integer> {

    @Query("SELECT po FROM PresentationOrder po WHERE po.sprint.project.id = :projectId")
    List<PresentationOrder> findAllByProject(Integer projectId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM presentation_orders WHERE sprint_id IN (SELECT id FROM sprints WHERE project_id = :projectId)", nativeQuery = true)
    void deleteAllByProject(Integer projectId);

    @Query("SELECT po FROM PresentationOrder po WHERE po.student.team.id = :teamId AND po.sprint.id = :sprintId ORDER BY po.value ASC")
    List<PresentationOrder> findByTeamIdAndSprintId(Integer teamId, Integer sprintId);
}
