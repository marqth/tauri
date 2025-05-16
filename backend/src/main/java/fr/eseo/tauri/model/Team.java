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
@Table(name = "teams", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "project_id"}))
@NoArgsConstructor
@RequiredArgsConstructor
@Data
public class Team implements Serializable {

    private static final long serialVersionUID = 2L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty
    private Integer id;

    @JsonProperty
    private String name;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "project_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty
    private Project project;

    @ManyToOne
    @JoinColumn(name = "leader_id")
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JsonProperty
    private User leader;

    @Transient
    @JsonDeserialize
    private Integer leaderId;

}

