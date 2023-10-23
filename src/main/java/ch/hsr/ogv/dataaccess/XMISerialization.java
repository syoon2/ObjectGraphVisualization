package ch.hsr.ogv.dataaccess;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import ch.hsr.ogv.model.ModelClass;
import ch.hsr.ogv.model.Relation;
import ch.hsr.ogv.util.MessageBar;
import ch.hsr.ogv.util.MessageBar.MessageLevel;

/**
 * This class represents a XMI Parser. It reads the Version of the XMI File and use an according handler. If no handler
 * for the version exists or the version is not supported an Error Message is created.
 *
 * @author Dario Vonaesch, Simon Gwerder
 * @version 3DCOV 3.0, May 2007 / OGV 3.1, May 2015
 */
public class XMISerialization implements SerializationStrategy {

    private final static Logger logger = LogManager.getLogger(SerializationStrategy.class);

    private static final String[] SUPPORTED_VERSIONS = {"1.1"};

    private SAXParser mParser;
    private VersionHandler mVersionHandler;
    private XMIHandler xmiHandler;

    /**
     * Constructor - initialises variables.
     */
    public XMISerialization() {
        xmiHandler = new XMIHandler();
    }

    /**
     * @return the read class associations
     */
    @Override
    public Set<Relation> getRelations() {
        return new LinkedHashSet<Relation>(xmiHandler.getRelations());
    }

    /**
     * @return the read classes
     */
    @Override
    public Set<ModelClass> getClasses() {
        return new LinkedHashSet<ModelClass>(xmiHandler.getClasses());
    }

    @Override
    public boolean parse(File file) {
        try {
            return parseXMI(file);
        }
        catch (SAXException | IOException | ParserConfigurationException e) {
            logger.catching(Level.DEBUG, e);
            MessageBar.setText("Unable to read XMI file: \"" + file.getPath() + "\".", MessageLevel.ALERT);
        }
        return false;
    }

    public boolean parseXMI(File xmiFile) throws SAXException, IOException, ParserConfigurationException {
        mParser = SAXParserFactory.newInstance().newSAXParser();
        mVersionHandler = new VersionHandler();
        try {
            mParser.parse(xmiFile, mVersionHandler);
        }
        catch (org.xml.sax.SAXParseException e) {
            logger.catching(Level.DEBUG, e);
        }
        String version = mVersionHandler.getVersion();
        ArrayList<String> supportedVersions = new ArrayList<String>(Arrays.asList(SUPPORTED_VERSIONS));
        // version not recognised
        if (version == null) {
            MessageBar.setText("Unable to read XMI file: \"" + xmiFile.getPath() + "\".", MessageLevel.ALERT);
        }
        else if (supportedVersions.contains(version)) { // parsing file
            xmiHandler = new XMI_1_1();
            try {
                mParser.parse(xmiFile, xmiHandler);
                return true;
            }
            catch (org.xml.sax.SAXParseException e) {
                logger.catching(Level.DEBUG, e);
                MessageBar.setText("Unable to read XMI file: \"" + xmiFile.getPath() + "\".", MessageLevel.ALERT);
            }
        }
        // version not supported
        else {
            xmiHandler = new XMIHandler();
            MessageBar.setText("The XMI version " + version + " is not supported. Supported XMI versions: " + String.join(",", SUPPORTED_VERSIONS), MessageLevel.ALERT);
        }
        return false;
    }

    @Override
    public boolean serialize(File file) {
        return false; // not supported (XMI export)
    }

    @Override
    public void setClasses(Set<ModelClass> modelClasses) {
        return; // not supported (XMI export)
    }

    @Override
    public void setRelations(Set<Relation> relations) {
        return; // not supported (XMI export)
    }

}