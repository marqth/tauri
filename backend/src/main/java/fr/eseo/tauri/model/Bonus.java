package fr.eseo.tauri.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;

@Entity
@Table(name = "bonuses", uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "sprint_id", "limited"}))
@NoArgsConstructor
@RequiredArgsConstructor
@Data
public class Bonus implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty
    private Integer id;

    @NonNull
    @JsonProperty
    private Float value;

    @JsonProperty
    private String comment;

    @NonNull
    @JsonProperty
    private Boolean limited;

    @ManyToOne
    @JoinColumn(name = "author_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty
    private User author;

    @ManyToOne
    @JoinColumn(name = "sprint_id")
    @NonNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty
    private Sprint sprint;

    @ManyToOne
    @JoinColumn(name = "student_id")
    @NonNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty
    private Student student;

    @Transient
    @JsonDeserialize
    private Integer authorId;

}