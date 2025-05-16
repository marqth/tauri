package fr.eseo.tauri.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import fr.eseo.tauri.model.ValidationFlag;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ValidationFlagRepository extends JpaRepository<ValidationFlag, Integer> {

    @Query("SELECT vf FROM ValidationFlag vf WHERE vf.author.id = :authorId AND vf.flag.id = :flagId")
    ValidationFlag findByAuthorIdAndFlagId(Integer flagId, Integer authorId);

    @Query("SELECT vf FROM ValidationFlag vf WHERE vf.flag.id = :flagId")
    List<ValidationFlag> findAllByFlag(Integer flagId);

}
