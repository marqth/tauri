package fr.eseo.tauri.repository;

import fr.eseo.tauri.model.enumeration.FlagType;
import org.springframework.data.jpa.repository.JpaRepository;
import fr.eseo.tauri.model.Flag;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FlagRepository extends JpaRepository<Flag, Integer> {

    @Query("SELECT f FROM Flag f WHERE f.project.id = :projectId")
    List<Flag> findAllByProject(Integer projectId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM flags WHERE project_id = :projectId", nativeQuery = true)
    void deleteAllByProject(Integer projectId);

    @Query("SELECT f FROM Flag f WHERE f.author.id = :authorId AND f.description = :description")
    List<Flag> findByAuthorIdAndDescription(Integer authorId, String description);

    @Query("SELECT f FROM Flag f WHERE f.author.id = :authorId AND f.type = :type")
    List<Flag> findByAuthorIdAndType(Integer authorId, FlagType type);

    @Query("SELECT f FROM Flag f WHERE f.firstStudent.team.id = :teamId OR f.secondStudent.team.id = :teamId")
    List<Flag> findByConcernedTeamId(Integer teamId);
}
