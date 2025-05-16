package fr.eseo.tauri.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;

@Data
public class Criteria {

    @NonNull
    @JsonProperty
    private Integer nbWomens;

    @NonNull
    @JsonProperty
    private Integer nbBachelors;

    @NonNull
    @JsonProperty
    private Boolean validCriteriaWoman;

    @NonNull
    @JsonProperty
    private Boolean validCriteriaBachelor;

}