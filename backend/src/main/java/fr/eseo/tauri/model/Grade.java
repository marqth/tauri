package fr.eseo.tauri.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.eseo.tauri.util.valid.Create;
import fr.eseo.tauri.util.valid.Update;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "grades", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_id", "sprint_id", "grade_type_id", "author_id"}),
        @UniqueConstraint(columnNames = {"team_id", "sprint_id", "grade_type_id", "author_id"})
})
@Data
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty
    private Integer id;

    @NotNull(groups = { Create.class }, message = "The value field is required")
    @Max(value = 20, groups = { Create.class, Update.class }, message = "The value field must be less than or equal to 20")
    @Min(value = 0, groups = { Create.class, Update.class }, message = "The value field must be greater than or equal to 0")
    @JsonProperty
    private Float value;

    @JsonProperty
    @Column(columnDefinition = "TEXT")
    private String comment;

    @ManyToOne
    @JoinColumn(name = "grade_type_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty
    private GradeType gradeType;

    @ManyToOne
    @JoinColumn(name = "author_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty
    private User author;

    @ManyToOne
    @JoinColumn(name = "student_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty
    private Student student;

    @ManyToOne
    @JoinColumn(name = "team_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty
    private Team team;

    @Column(name = "confirmed", nullable = true, columnDefinition = "BOOLEAN DEFAULT FALSE")
    @JsonProperty
    private Boolean confirmed = false;

    @ManyToOne
    @JoinColumn(name = "sprint_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty
    private Sprint sprint;

    @NotNull(groups = { Create.class }, message = "The gradeTypeId field is required")
    @Transient
    @JsonDeserialize
    private Integer gradeTypeId;

    @NotNull(groups = { Create.class }, message = "The authorId field is required")
    @Transient
    @JsonDeserialize
    private Integer authorId;

//    @NotNull(groups = { Create.class }, message = "The studentId field is required")
    @Transient
    @JsonDeserialize
    private Integer studentId;

//    @NotNull(groups = { Create.class }, message = "The teamId field is required")
    @Transient
    @JsonDeserialize
    private Integer teamId;

//    @NotNull(groups = { Create.class }, message = "The sprintId field is required")
    @Transient
    @JsonDeserialize
    private Integer sprintId;

}