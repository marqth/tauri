package fr.eseo.tauri.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomLogger {

    private static Logger logger = LoggerFactory.getLogger(CustomLogger.class);

    /**
     * Private constructor to prevent instantiation
     */
    CustomLogger() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Set the logger (for testing purposes)
     * @param logger the logger to set
     */
    static void setLogger(Logger logger) {
        CustomLogger.logger = logger;
    }

    /**
     * Log an info message
     * @param message the message to log
     */
    public static void info(String message) {
        logger.info(message);
    }

    /**
     * Log a warning message
     * @param message the message to log
     */
    public static void warn(String message) {
        logger.warn(message);
    }

    /**
     * Log an error message
     * @param message the message to log
     */
    public static void error(String message) {
        logger.error(message);
    }

    /**
     * Log an error message with a throwable
     * @param message the message to log
     * @param throwable the throwable to log
     */
    public static void error(String message, Throwable throwable) {
        logger.error(message, throwable);
    }

}