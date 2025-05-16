package fr.eseo.tauri.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import fr.eseo.tauri.model.GradeType;
import fr.eseo.tauri.model.enumeration.GradeTypeName;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface GradeTypeRepository extends JpaRepository<GradeType, Integer> {

	@Query("SELECT g FROM GradeType g WHERE g.forGroup")
	List<GradeType> findAllForGroup();


	@Query("SELECT gt FROM GradeType gt WHERE gt.imported = true AND gt.project.id = :projectId")
	List<GradeType> findAllImported(Integer projectId);

	@Query("SELECT gt FROM GradeType gt WHERE gt.imported = false AND gt.project.id = :projectId")
	List<GradeType> findAllUnimported(Integer projectId);

	@Query("SELECT gt FROM GradeType gt WHERE gt.name = :name")
	GradeType findByName(String name);

	@Query("SELECT g FROM GradeType g WHERE g.name = :name AND g.project.id = :projectId")
	GradeType findByNameAndProjectId(String name, Integer projectId);

	@Modifying
	@Transactional
	@Query("DELETE FROM GradeType gt WHERE gt.imported = true")
	void deleteAllImported();

	@Modifying
	@Transactional
	@Query("DELETE FROM GradeType gt WHERE gt.imported = false")
	void deleteAllUnimported();

	@Query("SELECT gt FROM GradeType gt WHERE gt.imported = false AND gt.forGroup = true")
	List<GradeType> findAllUnimportedAndForGroup();

	@Query("SELECT gt FROM GradeType gt WHERE gt.forGroup = true AND gt.imported = false AND gt.name != :globalTeamPerformance")
	List<GradeType> findTeacherGradedTeamGradeTypesWithoutGTPGrade(String globalTeamPerformance);

	default List<GradeType> findTeacherGradedTeamGradeTypes(){
		return findTeacherGradedTeamGradeTypesWithoutGTPGrade(GradeTypeName.GLOBAL_TEAM_PERFORMANCE.displayName());
	}
}
