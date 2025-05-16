package fr.eseo.tauri.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.eseo.tauri.util.valid.Create;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "comments", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_id", "sprint_id", "feedback", "author_id"}),
})
@Data
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty
    private Integer id;

    @NotNull(groups = { Create.class }, message = "The content field is required")
    @Column(columnDefinition = "TEXT")
    @JsonProperty
    private String content;

    @NotNull(groups = { Create.class }, message = "The feedback field is required")
    @JsonProperty
    private Boolean feedback;

    @ManyToOne
    @JoinColumn(name = "team_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty
    private Team team;

    @ManyToOne
    @JoinColumn(name = "student_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty
    private Student student;

    @ManyToOne
    @JoinColumn(name = "sprint_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty
    private Sprint sprint;

    @ManyToOne
    @JoinColumn(name = "author_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty
    private User author;

    @Transient
    @JsonDeserialize
    private Integer teamId;

    @Transient
    @JsonDeserialize
    private Integer studentId;

    @NotNull(groups = { Create.class }, message = "The sprintId field is required")
    @Transient
    @JsonDeserialize
    private Integer sprintId;

    @NotNull(groups = { Create.class }, message = "The authorId field is required")
    @Transient
    @JsonDeserialize
    private Integer authorId;

}