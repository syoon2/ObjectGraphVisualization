package ch.hsr.ogv.util;

import com.interactivemesh.jfx.importer.ImportException;
import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import javafx.scene.Node;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;

public class ObjModelLoader {

    private final static Logger logger = LogManager.getLogger(ObjModelLoader.class);

    public static Node[] load(URL modelUrl) {
        Node[] rootNodes = {};
        if (modelUrl != null) {
            ObjModelImporter tdsImporter = new ObjModelImporter();
            try {
                tdsImporter.read(modelUrl);
            }
            catch (ImportException e) {
                logger.catching(Level.DEBUG, e);
            }
            return tdsImporter.getImport();
        }
        return rootNodes;
    }

}
