package fr.eseo.tauri.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.eseo.tauri.model.id_class.ValidationFlagId;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "validation_flags", uniqueConstraints = @UniqueConstraint(columnNames = {"author_id", "flag_id"}))
@IdClass(ValidationFlagId.class)
@Data
public class ValidationFlag {

    @Id
    @ManyToOne
    @JoinColumn(name = "author_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty
    private User author;

    @JsonProperty
    private Boolean confirmed = false;

    @Id
    @ManyToOne
    @JoinColumn(name = "flag_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty
    private Flag flag;

    @Transient
    @JsonDeserialize
    private Integer authorId;

    @Transient
    @JsonDeserialize
    private Integer flagId;

}