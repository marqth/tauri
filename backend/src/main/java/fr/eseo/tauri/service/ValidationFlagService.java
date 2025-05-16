package fr.eseo.tauri.service;

import fr.eseo.tauri.model.Flag;
import fr.eseo.tauri.model.Student;
import fr.eseo.tauri.model.ValidationFlag;
import fr.eseo.tauri.model.enumeration.RoleType;
import fr.eseo.tauri.repository.ValidationFlagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ValidationFlagService {

    private final ValidationFlagRepository validationFlagRepository;
    private final UserService userService;
    private final TeamService teamService;

    public ValidationFlag getValidationFlagByAuthorId(Integer flagId, Integer authorId) {
        return validationFlagRepository.findByAuthorIdAndFlagId(flagId, authorId);
    }

    public List<ValidationFlag> getAllValidationFlags(Integer flagId) {
        return validationFlagRepository.findAllByFlag(flagId);
    }

    public void createValidationFlags(Flag flag) {
        if(userService.getRolesByUserId(flag.author().id()).contains(RoleType.OPTION_STUDENT)){
            List<Student> students = teamService.getStudentsByTeamId(flag.firstStudent().team().id(), false);
            students.addAll(teamService.getStudentsByTeamId(flag.secondStudent().team().id(), false));
            for(Student student: students){
                ValidationFlag validationFlag = new ValidationFlag();
                validationFlag.flag(flag);
                validationFlag.confirmed(null);
                validationFlag.author(student);
                validationFlagRepository.save(validationFlag);
            }
        }
    }

    public void updateValidationFlag(Integer flagId, Integer authorId, ValidationFlag updatedValidationFlag) {
        ValidationFlag validationFlag = getValidationFlagByAuthorId(flagId, authorId);

        if (updatedValidationFlag.confirmed() != null) validationFlag.confirmed(updatedValidationFlag.confirmed());
        validationFlagRepository.save(validationFlag);
    }

    public void createValidationFlag(Integer flagId, ValidationFlag validationFlag) {
        validationFlag.flag(new Flag().id(flagId));
        validationFlag.author(userService.getUserById(validationFlag.authorId()));
        validationFlagRepository.save(validationFlag);
    }
}