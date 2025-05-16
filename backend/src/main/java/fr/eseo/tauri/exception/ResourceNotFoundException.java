package fr.eseo.tauri.exception;

import fr.eseo.tauri.util.ListUtil;

import java.util.List;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, Integer id) {
        super("The " + resource + " with id " + id + " has not been found.");
    }

    public ResourceNotFoundException(String resource, Integer ...ids) {
        super("The " + resource + " with id (" + ListUtil.toString(List.of(ids)) + ") has not been found.");
    }

}
