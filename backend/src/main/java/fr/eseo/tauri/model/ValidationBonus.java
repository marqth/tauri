package fr.eseo.tauri.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.eseo.tauri.model.id_class.ValidationBonusId;
import fr.eseo.tauri.util.valid.Create;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "validation_bonuses", uniqueConstraints = @UniqueConstraint(columnNames = {"author_id", "bonus_id"}))
@IdClass(ValidationBonusId.class)
@Data
public class ValidationBonus {

    @Id
    @ManyToOne
    @JoinColumn(name = "author_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty
    private User author;

    @Id
    @ManyToOne
    @JoinColumn(name = "bonus_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty
    private Bonus bonus;

    @NotNull(groups = { Create.class }, message = "The authorId field is required")
    @Transient
    @JsonDeserialize
    private Integer authorId;


    @NotNull(groups = { Create.class }, message = "The bonusId field is required")
    @Transient
    @JsonDeserialize
    private Integer bonusId;


}