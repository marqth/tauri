package fr.eseo.tauri.repository;

import fr.eseo.tauri.model.enumeration.Gender;
import org.springframework.data.jpa.repository.JpaRepository;
import fr.eseo.tauri.model.Student;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Integer> {

    @Query("SELECT s FROM Student s WHERE s.project.id = :projectId")
    List<Student> findAllByProject(Integer projectId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM students WHERE project_id = :projectId", nativeQuery = true)
    void deleteAllByProject(Integer projectId);

    List<Student> findByTeamName(String teamName);

    Student findByName(String studentName);

    @Query("SELECT s FROM Student s WHERE s.team.id = :teamId")
    List<Student> findByTeam(Integer teamId);

    List<Student> findByGender(Gender gender);
    @Query("SELECT s FROM Student s WHERE s.gender = :gender AND s.project.id = :projectId")
    List<Student> findByGenderAndProjectId(Gender gender, Integer projectId);

    List<Student> findByGenderOrderByBachelor(Gender gender);


    @Query("SELECT s FROM Grade gr JOIN gr.student s JOIN gr.gradeType gt WHERE (gt.name = 'Moyenne' AND (s.gender = :gender OR s.gender IS NULL) AND s.project.id = :projectId) ORDER BY s.bachelor, gr.value DESC")
    List<Student> findByGenderOrderByBachelorAndImportedAvgDesc(Gender gender, Integer projectId);

    @Transactional
    @Modifying
    @Query("UPDATE Student s SET s.team = null WHERE s.project.id = :projectId")
    void removeAllStudentsFromTeams(Integer projectId);

	@Query("SELECT COUNT(s) FROM Student s WHERE s.gender = 'WOMAN'")
	int countWomen();

	@Query("SELECT COUNT(s) FROM Student s")
	int countTotal();

	@Query("SELECT COUNT(s) FROM Student s WHERE s.bachelor = true")
	int countBachelor();

}
