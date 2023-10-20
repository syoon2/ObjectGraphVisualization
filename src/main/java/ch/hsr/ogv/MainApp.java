package ch.hsr.ogv;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Locale;
import java.util.Properties;

import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.hsr.ogv.util.ResourceLocator;
import ch.hsr.ogv.util.ResourceLocator.Resource;

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

    private final static UncaughtExceptionHandler ueHandler = (thread, throwable) -> {
        logger.catching(Level.DEBUG ,throwable);
    };

    public static void main(String[] args) {
        // System.setProperty("prism.lcdtext", "false");
        // System.setProperty("prism.text", "t2k");
        // System.setProperty("prism.marlin", "true");
        Locale.setDefault(new Locale("en", "EN")); // set to English
        Font.loadFont(ResourceLocator.getResourcePath(Resource.SEGOEUI_TTF).toExternalForm(), Font.getDefault().getSize());
        Font.loadFont(ResourceLocator.getResourcePath(Resource.LUCIDASANS_TTF).toExternalForm(), Font.getDefault().getSize());
        Thread.setDefaultUncaughtExceptionHandler(ueHandler);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Thread.currentThread().setUncaughtExceptionHandler(ueHandler);
        new StageBuilder(primaryStage);
    }

}
