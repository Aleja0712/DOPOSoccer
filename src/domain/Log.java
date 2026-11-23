package domain;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Registro de errores para los programadores de Soccer.
 */
public class Log {
    public static final String NAME = "Soccer";

    public static void record(Exception e) {
        try {
            Logger logger = Logger.getLogger(NAME);
            logger.setUseParentHandlers(false);
            FileHandler file = new FileHandler(NAME + ".log", true);
            file.setFormatter(new SimpleFormatter());
            logger.addHandler(file);
            logger.log(Level.SEVERE, e.toString(), e);
            file.close();
        } catch (Exception oe) {
            oe.printStackTrace();
        }
    }
}