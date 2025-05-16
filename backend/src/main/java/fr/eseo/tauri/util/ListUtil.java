package fr.eseo.tauri.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class ListUtil {

	private ListUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Filter a list
	 * @param list the list to filter
	 * @param predicate the predicate to apply to each element
	 * @return the filtered list
	 * @param <T> the type of the list
	 */
	public static <T> List<T> filter(List<T> list, Predicate<T> predicate) {
		var filteredList = new ArrayList<T>();
		for (var element : list) {
			if (predicate.test(element)) {
				filteredList.add(element);
			}
		}
		return filteredList;
	}

	/**
	 * Map a list to another list
	 * @param list the list to map
	 * @param function the function to apply to each element
	 * @return the mapped list
	 * @param <T> the type of the input list
	 * @param <R> the type of the output list
	 */
	public static <T, R> List<R> map(List<T> list, Function<T, R> function) {
		var mappedList = new ArrayList<R>();
		for (var element : list) {
			mappedList.add(function.apply(element));
		}
		return mappedList;
	}

	/**
	 * Check if a list contains a value
	 * @param list the list to check
	 * @param value the value to check
	 * @return true if the list contains the value, false otherwise
	 * @param <T> the type of the list
	 */
	public static <T> boolean contains(List<T> list, T value) {
		for (var element : list) {
			if (element.equals(value)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Convert a list to a string
	 * @param list the list to convert
	 * @return the string which format is "element1, element2, ..."
	 * @param <T> the type of the list
	 */
	public static <T> String toString(List<T> list) {
		var builder = new StringBuilder();

		for (var element : list) {
			builder.append(element.toString());
			builder.append(", ");
		}

		if (!list.isEmpty()) {
			builder.delete(builder.length() - 2, builder.length());
		}

		return builder.toString();
	}

}
