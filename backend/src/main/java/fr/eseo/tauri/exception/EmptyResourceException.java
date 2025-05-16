package fr.eseo.tauri.exception;

public class EmptyResourceException  extends RuntimeException {

    public EmptyResourceException(String resource) {
        super("The " + resource + " is empty or null.");
    }
}
