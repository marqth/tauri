package fr.eseo.tauri.util;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ResponseMessage {

	private static final String CREATE_MESSAGE = "The %s has been created";
	private static final String UPDATE_MESSAGE = "The %s has been updated";
	private static final String DELETE_MESSAGE = "The %s has been deleted";
	private static final String DELETE_ALL_MESSAGE = "Every %s has been deleted";
	private static final String DELETE_ALL_FROM_CURRENT_PROJECT_MESSAGE = "Every %s of the current project has been deleted";

	// The resource for which the message is created
	private final String resource;

	/**
	 * Creates a formatted message indicating the resource has been created.
	 * @return A string message indicating the resource has been created.
	 */
	public String create() {
		return String.format(CREATE_MESSAGE, resource);
	}

	/**
	 * Creates a formatted message indicating the resource has been updated.
	 * @return A string message indicating the resource has been updated.
	 */
	public String update() {
		return String.format(UPDATE_MESSAGE, resource);
	}

	/**
	 * Creates a formatted message indicating the resource has been deleted.
	 * @return A string message indicating the resource has been deleted.
	 */
	public String delete() {
		return String.format(DELETE_MESSAGE, resource);
	}

	/**
	 * Creates a formatted message indicating all instances of the resource have been deleted.
	 * @return A string message indicating all instances of the resource have been deleted.
	 */
	public String deleteAll() {
		return String.format(DELETE_ALL_MESSAGE, resource);
	}

	/**
	 * Creates a formatted message indicating all instances of the resource from the current project have been deleted.
	 * @return A string message indicating all instances of the resource from the current project have been deleted.
	 */
	public String deleteAllFromCurrentProject() {
		return String.format(DELETE_ALL_FROM_CURRENT_PROJECT_MESSAGE, resource);
	}

	/**
	 * Creates a custom formatted message for the resource.
	 * @param message The custom message to be formatted with the resource name replacing the %s.
	 * @return A string message formatted with the custom message and the resource.
	 */
	public String custom(String message) {
		return String.format(message, resource);
	}



}
