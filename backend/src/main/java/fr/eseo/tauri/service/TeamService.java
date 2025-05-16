package fr.eseo.tauri.service;

import fr.eseo.tauri.model.*;
import fr.eseo.tauri.model.enumeration.Gender;
import fr.eseo.tauri.model.enumeration.GradeTypeName;
import fr.eseo.tauri.model.enumeration.RoleType;
import fr.eseo.tauri.repository.*;
import fr.eseo.tauri.util.CustomLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import fr.eseo.tauri.exception.ResourceNotFoundException;
import fr.eseo.tauri.repository.TeamRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserService userService;
    private final ProjectService projectService;
    private final StudentRepository studentRepository;
    private final RoleRepository roleRepository;
    private final CommentRepository commentRepository;
    private final GradeRepository gradeRepository;
    private final GradeTypeRepository gradeTypeRepository;
    private final PresentationOrderService presentationOrderService;
    @Lazy
    private final SprintService sprintService;
    @Lazy
    private final StudentService studentService;

    public Team getTeamById(Integer id) {
        return teamRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("team", id));
    }

    public List<Team> getAllTeamsByProject(Integer projectId) {
        return teamRepository.findAllByProject(projectId);
    }

    /**
     * Delete already existing teams in te project and then create teams with the given number of teams.
     * @param nbTeams the number of teams to create
     * @return a List<Teams> if teams are created, otherwise null
     */
    public List<Team> createTeams(Integer projectId, Integer nbTeams) {
        if (nbTeams < 1) {
            CustomLogger.error("TeamService.createTeams : The number of teams to create must be greater than 0");
            throw new IllegalArgumentException("The number of teams to create must be greater than 0");
        }

        Project project = projectService.getProjectById(projectId);

        // Delete all previous teams
        if(!getAllTeamsByProject(projectId).isEmpty()){
            deleteAllTeamsByProject(projectId);
        }

        ArrayList<Team> teams = new ArrayList<>();

        // Create the teams
        for (int i = 0; i < nbTeams; i++) {
            Team team = new Team(project);
            team.name("Équipe " + (i + 1));
            this.teamRepository.save(team);
            teams.add(team);
        }

        return teams;
    }

    public void updateTeam(Integer id, Team updatedTeam) {
        Team team = getTeamById(id);

        if (null != updatedTeam.name()) team.name(updatedTeam.name());
        if (updatedTeam.leaderId() != null) team.leader(userService.getUserById(updatedTeam.leaderId()));

        teamRepository.save(team);
    }

    public void deleteAllTeamsByProject(Integer projectId) {
        studentRepository.removeAllStudentsFromTeams(projectId);
        teamRepository.deleteAllByProject(projectId);
    }

    /**
     * This method retrieves the number of women in a team by the team's ID.
     *
     * @param id The ID of the team.
     * @return The number of women in the team if the team exists, otherwise 0.
     */
    public Integer getNbWomenByTeamId(Integer id){
        getTeamById(id);
        return teamRepository.countWomenInTeam(id);
    }

    /**
     * This method retrieves the number of bachelor students in a team by the team's ID.
     *
     * @param id The ID of the team.
     * @return The number of bachelor students in the team if the team exists, otherwise 0.
     */
    public Integer getNbBachelorByTeamId(Integer id){
        getTeamById(id);
        return teamRepository.countBachelorInTeam(id);
    }

    public List<Student> getStudentsByTeamId(Integer id, Boolean ordered) {
        Team team = getTeamById(id);
        if(Boolean.TRUE.equals(ordered)) {
            Sprint currentSprint = sprintService.getCurrentSprint(team.project().id());
            CustomLogger.info(currentSprint.id().toString());
            var students = studentRepository.findByTeam(id);
            if(currentSprint != null){
                var presentationOrder = presentationOrderService.getPresentationOrderByTeamIdAndSprintId(id, currentSprint.id());
                students.sort(Comparator.comparingInt(presentationOrder::indexOf));
                //CustomLogger.info(students.get(0).id.toString());
                return students;
            }
        }
        return studentRepository.findByTeam(id);
    }

    public Double getTeamAvgGrade(Integer id) {
        Team team = getTeamById(id);
        return teamRepository.findAvgGradeByTeam(team);
    }

    public Criteria getCriteriaByTeamId(Integer id, Integer projectId) {
        getTeamById(id);
        boolean validateWoman = getNbWomenByTeamId(id) >= projectService.getProjectById(projectId).nbWomen();
        boolean validateBachelor = getNbBachelorByTeamId(id) >= 1;
        return new Criteria(getNbWomenByTeamId(id), getNbBachelorByTeamId(id), validateWoman, validateBachelor);
    }

    /**
     * Auto generate teams with students according to the given number of teams and the number of women per team.
     * FUTURE :  create teams with the same average grade
     */
    public void generateTeams(Integer projectId, Project projectDetails, boolean autoWomenRatio) {
        CustomLogger.info("TeamService.createTeams : Creating Teams");

        List<Student> women = this.studentRepository.findByGenderAndProjectId(Gender.WOMAN, projectId);
        List<Student> men = this.studentRepository.findByGenderOrderByBachelorAndImportedAvgDesc(Gender.MAN, projectId);
        int nbStudent = men.size() + women.size();
        Integer nbTeams = projectDetails.nbTeams();
        Integer womenPerTeam = projectDetails.nbWomen();

        // Check if the number of students is enough to create the teams
        if (nbStudent < nbTeams * womenPerTeam - 1) {
            CustomLogger.error("TeamService.generateTeams : Not enough students to create the teams");
            throw new IllegalArgumentException("Not enough students to create the teams");
        }
        projectService.updateProject(projectId, projectDetails);
        List<Team> teams = this.createTeams(projectId, nbTeams);
        this.fillTeams(teams, women, men, womenPerTeam, autoWomenRatio, projectId);
    }

    /**
     * Assign teams to the students.
     * @param teams the list of empty teams to fill
     * @param women the list of women students
     * @param men the list of men students
     * @param womenPerTeam the number of women per team
     */
    public void fillTeams(List<Team> teams, List<Student> women, List<Student> men, Integer womenPerTeam, boolean autoWomenRatio, Integer projectId) {
        int nbTeams = teams.size();

        List<Team> sortedTeams = teams;
        int index = 0;

        if (!autoWomenRatio && womenPerTeam != 0) {
            assignWomenPerTeam(teams, women, men, womenPerTeam);

            // re-order the teams by average grade
            sortedTeams = this.teamRepository.findAllOrderByAvgGradeOrderByAsc(projectId);

            // find the next index
            index = nbTeams * womenPerTeam;
        }

        // Assign the remaining students evenly to the teams
        assignStudentsEvenly(women, men, projectId, index, sortedTeams);
        CustomLogger.info("Teams have been filled with students");
    }

    /**
     * Assign "womenPerTeam" women to the teams first then even the teams with men if needed
     * @param teams the list of teams
     * @param women the list of female students
     * @param men   the list of male students
     * @param womenPerTeam  the number of women to assign to every team
     */
    public void assignWomenPerTeam(List<Team> teams, List<Student> women, List<Student> men, Integer womenPerTeam) {
        int nbTeams = teams.size();
        int nbWomen = women.size();
        int nbStudents = nbWomen + men.size();

        int index;
        for (int i = 0; i < nbTeams; i++) {
            for (int j = 0; j < womenPerTeam; j++) {
                Student student;
                Role role = new Role();
                role.type(RoleType.TEAM_MEMBER);
                index = i * womenPerTeam + j;

                if (index < nbWomen) {
                    student = women.get(index);
                    student.team(teams.get(i));
                    role.user(student);
                    this.roleRepository.save(role);
                    this.studentRepository.save(student);
                } else if (index < nbStudents) {
                    student = men.get(index - nbWomen);
                    student.team(teams.get(i));
                    role.user(student);
                    this.roleRepository.save(role);
                    this.studentRepository.save(student);
                }
            }
        }
    }

    /**
     * Assign the remaining students evenly to the teams
     * @param women the list of female students
     * @param men   the list of male students
     * @param projectId the ID of the project
     * @param index the index of the first student to assign
     * @param sortedTeams the list of teams sorted by average grade
     */
    public void assignStudentsEvenly(List<Student> women, List<Student> men, Integer projectId, int index, List<Team> sortedTeams) {
        int nbTeams = sortedTeams.size();
        int nbWomen = women.size();
        int nbStudent = nbWomen + men.size();

        for (int i = index; i < nbStudent; i++) {
            if (((i - index) % nbTeams == 0) && i != 0) {
                sortedTeams = this.teamRepository.findAllOrderByAvgGradeOrderByAsc(projectId);
            }

            CustomLogger.info("Sorted teams : " + sortedTeams);

            Student student;
            Role role = new Role();
            role.type(RoleType.TEAM_MEMBER);

            if (i < nbWomen) {
                student = women.get(i);
                student.team(sortedTeams.get((i - index)% nbTeams));
            }else{
                student = men.get(i - nbWomen);
                student.team(sortedTeams.get((i - index)% nbTeams));
            }

            role.user(student);
            CustomLogger.info("studentEND : " + student);
            this.roleRepository.save(role);
            this.studentRepository.save(student);
        }
    }

    public List<Comment> getFeedbacksByTeamAndSprint(Integer teamId, Integer sprintId) {
        return commentRepository.findAllByTeamIdAndSprintId(teamId, sprintId);
    }

    public Double getTeamTotalGrade(Integer teamId, Integer sprintId) {
        List<GradeType> teacherGradedTeamGradeTypes = gradeTypeRepository.findTeacherGradedTeamGradeTypes();
        List<Double> teamGrades = new ArrayList<>();

        for (GradeType gradeType : teacherGradedTeamGradeTypes) {
            Double average = gradeRepository.findAverageByGradeTypeForTeam(teamId, sprintId, gradeType.name());
            if (average != null) {
                teamGrades.add(average);
            }

        }

        return formattedResult(teamGrades.stream().mapToDouble(Double::doubleValue).sum() / teamGrades.size());

    }

    public List<Double> getIndividualTotalGrades(Integer id, Integer sprintId) {

        List<Student> students = getStudentsByTeamId(id, false);
        List<Double> individualGrades = new ArrayList<>();

        for(Student student : students){
            Double studentGradedTeamGrade = gradeRepository.findAverageByGradeTypeForTeam(id, sprintId, GradeTypeName.GLOBAL_TEAM_PERFORMANCE.displayName());
            Double individualGrade = gradeRepository.findAverageByGradeTypeForStudent(student.id(), sprintId, GradeTypeName.INDIVIDUAL_PERFORMANCE.displayName());
            double result;
            if(studentGradedTeamGrade != null && individualGrade != null){
                result = (2*individualGrade + studentGradedTeamGrade)/3;
            }
            else if(studentGradedTeamGrade == null){
                result = individualGrade;
            } else {
                result = studentGradedTeamGrade;
            }
            individualGrades.add(formattedResult(result));

        }
        return individualGrades;
    }

    public List<Double> getSprintGrades(Integer id, Integer sprintId) {
        double teamGrade = getTeamTotalGrade(id, sprintId);

        List<Student> students = getStudentsByTeamId(id, false);
        List<Double> sprintGrades = new ArrayList<>();

        for(int i = 0; i < students.size(); i++){
            List<Bonus> studentBonuses = studentService.getStudentBonuses(students.get(i).id(), sprintId);
            double result = 0.7*(Math.min(teamGrade + studentBonuses.stream().mapToDouble(Bonus::value).sum(), 20.0)) + 0.3*(getIndividualTotalGrades(id, sprintId)).get(i);
            sprintGrades.add(formattedResult(result));
        }
        if (sprintGrades.isEmpty()) {
            return Collections.singletonList(-1.0);
        }
        return sprintGrades;
    }

    public List<Double> getAverageSprintGrades(Integer sprintId){
        List<Team> teams = teamRepository.findAll();
        List<Double> averageSprintGrades = new ArrayList<>();
        for(Team team : teams){
            List<Double> sprintGrades = getSprintGrades(team.id(), sprintId);
            double average = sprintGrades.stream().mapToDouble(Double::doubleValue).sum() / sprintGrades.size();
            averageSprintGrades.add(formattedResult(average));
        }
        return averageSprintGrades;
    }

    private Double formattedResult(Double result) {
        return Double.parseDouble(String.format("%.2f", result).replace(',', '.'));
    }

    public List<Comment> getIndividualCommentsByTeamIdAndSprintId(int teamId, int sprintId) {
        return this.commentRepository.findIndividualCommentsByTeamIdAndSprintId(teamId, sprintId);
    }

}