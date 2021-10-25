package se.company.services.utils;

import org.apache.logging.log4j.LogManager;

public class Logger {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger();

    public static void debug(String message) {
        System.out.println(message);
        logger.debug(message);
    }

    public static void info(String message) {
        System.out.println(message);
        logger.info(message);
    }

    public static void warn(String message) {
        System.out.println(message);
        logger.warn(message);
    }

    public static void error(String message) {
        System.out.println(message);
        logger.error(message);
    }
}
