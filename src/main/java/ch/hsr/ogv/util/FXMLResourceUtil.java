package ch.hsr.ogv.util;

import ch.hsr.ogv.util.ResourceLocator.Resource;
import javafx.fxml.FXMLLoader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

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
