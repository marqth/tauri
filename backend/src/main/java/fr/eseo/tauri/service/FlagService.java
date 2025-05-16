package fr.eseo.tauri.service;

import fr.eseo.tauri.exception.ResourceNotFoundException;
import fr.eseo.tauri.model.Flag;
import fr.eseo.tauri.model.enumeration.FlagType;
import fr.eseo.tauri.repository.FlagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FlagService {

    private final FlagRepository flagRepository;
    private final UserService userService;
    private final StudentService studentService;
    private final ProjectService projectService;
    private final ValidationFlagService validationFlagService;

    public Flag getFlagById(Integer id) {
        return flagRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("flag", id));
    }

    public List<Flag> getAllFlagsByProject(Integer projectId) {
        return flagRepository.findAllByProject(projectId);
    }

    public void createFlag(Flag flag) {
        flag.author(userService.getUserById(flag.authorId()));
        flag.project(projectService.getProjectById(flag.projectId()));
        if (flag.firstStudentId() != null) flag.firstStudent(studentService.getStudentById(flag.firstStudentId()));
        if (flag.secondStudentId() != null) flag.secondStudent(studentService.getStudentById(flag.secondStudentId()));

        flagRepository.save(flag);

        validationFlagService.createValidationFlags(flag);
    }

    public void updateFlag(Integer id, Flag updatedFlag) {
        Flag flag = getFlagById(id);

        if (updatedFlag.description() != null) flag.description(updatedFlag.description());
        if (updatedFlag.type() != null) flag.type(updatedFlag.type());
        if (updatedFlag.status() != null) flag.status(updatedFlag.status());
        if (updatedFlag.firstStudentId() != null) flag.firstStudent(studentService.getStudentById(updatedFlag.firstStudentId()));
        if (updatedFlag.secondStudentId() != null) flag.secondStudent(studentService.getStudentById(updatedFlag.secondStudentId()));
        if (updatedFlag.authorId() != null) flag.author(userService.getUserById(updatedFlag.authorId()));
        if (updatedFlag.projectId() != null) flag.project(projectService.getProjectById(updatedFlag.projectId()));

        flagRepository.save(flag);
    }

    public void deleteFlag(Integer id) {
        getFlagById(id);
        flagRepository.deleteById(id);
    }

    public void deleteAllFlagsByProject(Integer projectId) {
        flagRepository.deleteAllByProject(projectId);
    }

	public List<Flag> getFlagsByAuthorAndType(Integer authorId , FlagType type) {
        return flagRepository.findByAuthorIdAndType(authorId, type);
	}

    public List<Flag> getFlagsByConcernedTeamId(Integer teamId) {
        return flagRepository.findByConcernedTeamId(teamId);
    }
}