package ch.hsr.ogv;

import java.util.Locale;

import javafx.application.Application;
import javafx.scene.text.Font;

import org.apache.logging.log4j.LogManager;

import ch.hsr.ogv.util.ResourceLocator;

/**
 * Starts the application.
 */
public class Main {

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            LogManager.getLogger(MainApp.class).catching(throwable);
        });
        Locale.setDefault(Locale.ENGLISH); // set to English
        Font.loadFont(ResourceLocator.getResourcePath(ResourceLocator.Resource.SEGOEUI_TTF).toExternalForm(), Font.getDefault().getSize());
        Font.loadFont(ResourceLocator.getResourcePath(ResourceLocator.Resource.LUCIDASANS_TTF).toExternalForm(), Font.getDefault().getSize());
        Application.launch(MainApp.class, args);
    }

}
