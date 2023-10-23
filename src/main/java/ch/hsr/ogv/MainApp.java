package ch.hsr.ogv;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

import javafx.application.Application;
import javafx.stage.Stage;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Starts the FX application.
 */
public class MainApp extends Application {

    public static final Properties gitProperties;
    static {
        gitProperties = new Properties();
        try (InputStream inputStream = MainApp.class.getClassLoader().getResourceAsStream("git.properties")) {
            gitProperties.load(inputStream);
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    } 

    private final static Logger logger = LogManager.getLogger(MainApp.class);

    private final static Thread.UncaughtExceptionHandler ueHandler = (thread, throwable) -> {
        logger.catching(Level.DEBUG ,throwable);
    };

    @Override
    public void start(Stage primaryStage) {
        Thread.currentThread().setUncaughtExceptionHandler(ueHandler);
        new StageBuilder(primaryStage);
    }

}
