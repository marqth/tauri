package fr.eseo.tauri.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import fr.eseo.tauri.model.Sprint;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SprintRepository extends JpaRepository<Sprint, Integer> {

    @Query("SELECT s FROM Sprint s WHERE s.project.id = :projectId ORDER BY s.sprintOrder ASC")
    List<Sprint> findAllByProject(Integer projectId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM sprints WHERE project_id = :projectId", nativeQuery = true)
    void deleteAllByProject(Integer projectId);

}
