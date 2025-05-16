package fr.eseo.tauri.service;

import fr.eseo.tauri.model.Bonus;
import fr.eseo.tauri.model.PresentationOrder;
import fr.eseo.tauri.model.Sprint;
import fr.eseo.tauri.model.Student;
import fr.eseo.tauri.model.Comment;
import fr.eseo.tauri.exception.ResourceNotFoundException;
import fr.eseo.tauri.repository.CommentRepository;
import fr.eseo.tauri.repository.SprintRepository;
import fr.eseo.tauri.util.CustomLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SprintService {

    private final SprintRepository sprintRepository;
    private final ProjectService projectService;
    @Lazy
    private final StudentService studentService;
    private final PresentationOrderService presentationOrderService;
    private final BonusService bonusService;
    private final CommentRepository commentRepository;
    @Lazy
    private final TeamService teamService;

    public Sprint getSprintById(Integer id) {

        return sprintRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("sprint", id));
    }

    public List<Sprint> getAllSprintsByProject(Integer projectId) {
        return sprintRepository.findAllByProject(projectId);
    }

    public void createSprint(Sprint sprint, int sprintId) {
        CustomLogger.info("Creating sprint " + sprintId);

        sprint.project(projectService.getProjectById(sprint.projectId()));
        sprintRepository.save(sprint);
		List<Student> students = studentService.getAllStudentsByProject(sprint.projectId());
        if(!students.isEmpty()) {
            var teamsIndexes = new HashMap<Integer, Integer>();
            for (var team : teamService.getAllTeamsByProject(sprint.projectId())) {
                teamsIndexes.put(team.id(), 0);
            }
            for (Student student : students) {
				var presentationOrder = new PresentationOrder(sprint, student);
				var value = teamsIndexes.get(student.team().id());
				presentationOrder.value(value);
				teamsIndexes.put(student.team().id(), value + 1);
				presentationOrderService.createPresentationOrder(presentationOrder);
                Bonus limitedBonus = new Bonus((float) 0, true, sprint, student);
                Bonus unlimitedBonus = new Bonus((float) 0, false, sprint, student);
                bonusService.createBonus(limitedBonus);
                bonusService.createBonus(unlimitedBonus);
            }
        }
    }

    public void updateSprint(Integer id, Sprint updatedSprint) {
        Sprint sprint = getSprintById(id);

        if (updatedSprint.startDate() != null) sprint.startDate(updatedSprint.startDate());
        if (updatedSprint.endDate() != null) sprint.endDate(updatedSprint.endDate());
        if (updatedSprint.endType() != null) sprint.endType(updatedSprint.endType());
        if (updatedSprint.sprintOrder() != null) sprint.sprintOrder(updatedSprint.sprintOrder());
        if (updatedSprint.projectId() != null) sprint.project(projectService.getProjectById(updatedSprint.projectId()));

        sprintRepository.save(sprint);
    }

    public void deleteSprint(Integer id) {
        var deletedSprint = getSprintById(id);
        sprintRepository.deleteById(id);

        var sprints = sprintRepository.findAllByProject(id);
        for (var sprint : sprints) {
            if (sprint.sprintOrder() > deletedSprint.sprintOrder()) {
                sprint.sprintOrder(sprint.sprintOrder() - 1);
                sprintRepository.save(sprint);
            }
        }
    }

    public void deleteAllSprintsByProject(Integer projectId) {
        sprintRepository.deleteAllByProject(projectId);
    }

    public Sprint getCurrentSprint(Integer projectId) {
        LocalDate today = LocalDate.now();
        List<Sprint> sprints = sprintRepository.findAllByProject(projectId);

        Sprint currentSprint = null;
        Sprint closestSprint = null;
        long closestDays = Long.MAX_VALUE;

        for (Sprint sprint : sprints) {
            if (!sprint.startDate().isAfter(today) && !sprint.endDate().isBefore(today)) {
                currentSprint = sprint;
                break;
            }

            long daysToEndDate = ChronoUnit.DAYS.between(today, sprint.endDate());
            if (daysToEndDate < closestDays) {
                closestDays = daysToEndDate;
                closestSprint = sprint;
            }
        }

        return currentSprint != null ? currentSprint : closestSprint;
    }

    public List<Comment> getTeamStudentsComments(Integer sprintId, Integer authorId, Integer teamId){

        List<Comment> studentsComments = new ArrayList<>();
        List<Student> students = teamService.getStudentsByTeamId(teamId, true);

        for(Student student : students){
            studentsComments.addAll(commentRepository.findAllByTeamAndSprintAndAuthor(student.id(), sprintId, authorId));
        }

        return studentsComments;
    }
}