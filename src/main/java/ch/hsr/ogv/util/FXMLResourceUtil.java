package ch.hsr.ogv.util;

import java.io.IOException;

import javafx.fxml.FXMLLoader;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.hsr.ogv.util.ResourceLocator.Resource;

public class FXMLResourceUtil {

    private final static Logger logger = LogManager.getLogger(FXMLResourceUtil.class);

    public static Object loadPreset(Resource resource) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ResourceLocator.getResourcePath(resource));
        try {
            return loader.load();
        }
        catch (IOException e) {
            logger.catching(Level.DEBUG, e);
        }
        return null;
    }

    public static FXMLLoader prepareLoader(Resource resource) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ResourceLocator.getResourcePath(resource));
        return loader;
    }

}
