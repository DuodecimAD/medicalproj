package com.medical.projet.java.utility;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

// TODO: Auto-generated Javadoc
/**
 * The Class LoggerUtil.
 */
public class LoggerUtil {

    /** The logger. */
    private static Logger logger = Logger.getLogger(LoggerUtil.class.getName());

    /**
     * Gets the logger.
     *
     * @return the logger
     */
    public static Logger getLogger() {

        return logger;
    }


    /**
     * Setup logging.
     */
    public static void setupLogging() {

        File logFolder = new File(System.getProperty("user.home") + "/documents/com.myjavafxtemplate.myapp/logs/");
        if (!logFolder.exists()) {
            if (logFolder.mkdirs()) {
                logger.info("Log folder created");
            } else {
                logger.warning("Failed to create log folder");
            }
        }


        try {
            // this remoive the console spam
            Logger globalLogger = Logger.getLogger("");
            Handler[] handlers = globalLogger.getHandlers();
            for (Handler handler : handlers) {
                globalLogger.removeHandler(handler);
            }

            // Create a file handler
            // String fullPath = LoggerUtil.class.getClassLoader().getResource("com/myjavafxtemplate/myapp/").getPath().replaceFirst("^/(.:/)", "$1");
            FileHandler fileHandler = new FileHandler(System.getProperty("user.home") + "/documents/com.myjavafxtemplate.myapp/logs/log%g.log", 50000, 20, true);


            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new SimpleFormatter());

            // Add the handlers to the root logger
            logger.addHandler(fileHandler);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
