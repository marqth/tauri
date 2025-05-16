package fr.eseo.tauri.model.id_class;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.eseo.tauri.model.Flag;
import fr.eseo.tauri.model.User;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
public class ValidationFlagId implements Serializable {

    @JsonProperty
    private User author;

    @JsonProperty
    private Flag flag;

}
