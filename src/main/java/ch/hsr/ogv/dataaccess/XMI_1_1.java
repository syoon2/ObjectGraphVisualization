package ch.hsr.ogv.dataaccess;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.Attributes;

import ch.hsr.ogv.model.Attribute;
import ch.hsr.ogv.model.ModelClass;
import ch.hsr.ogv.model.RelationType;

/**
 * This Class is a handler for parsing XMI 1.1 files from Enterprise Architect.
 *
 * @author Dario Vonaesch, Simon Gwerder
 * @version 3DCOV 3.0, May 2007 / OGV 3.1, May 2015
 */
public class XMI_1_1 extends XMIHandler {

    private ArrayList<ModelClass> classes = new ArrayList<ModelClass>();

    private boolean inDependencyClient = false;
    private boolean inDependencySupplier = false;
    private boolean scaling = false;
    private boolean source = true;

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void endElement(String pUri, String pLName, String pQName) {
        if (pQName.equals("UML:Class")) {
            characters.delete(0, characters.length());
        }
        else if (pQName.equals("UML:Attribute")) {
            characters.delete(0, characters.length());
        }
        else if (pQName.equals("UML:Association")) {
            characters.delete(0, characters.length());
        }
        else if (pQName.equals("UML:Generalization")) {
            characters.delete(0, characters.length());
        }
        else if (pQName.equals("UML:Dependency")) {
            characters.delete(0, characters.length());
        }
        else if (pQName.equals("UML:Dependency.supplier")) {
            inDependencySupplier = false;
            characters.delete(0, characters.length());
        }
        else if (pQName.equals("UML:Dependency.client")) {
            inDependencyClient = false;
            characters.delete(0, characters.length());
        }

        else if (pQName.equals("UML:AssociationEnd")) {
            characters.delete(0, characters.length());
        }
        else if (pQName.equals("UML:DiagramElement")) {
            characters.delete(0, characters.length());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String pUri, String pLName, String pQName, Attributes pAtts) {
        characters.delete(0, characters.length());

        if (pQName.equals("UML:Class") || pQName.equals("UML:Interface")) {
            ModelClass modelClass = new ModelClass();
            String name = pAtts.getValue("name");
            String classId = pAtts.getValue("xmi.id");
            if (name != null && !name.isEmpty() && !name.equals("EARootClass")) {
                modelClass.setName(name);
            }
            else {
                return;
            }
            if (classId != null && !classId.isEmpty()) {
                idClassMap.put(classId, modelClass);
                classes.add(modelClass);
            }
        }
        else if (pQName.equals("UML:Attribute")) {
            String name = pAtts.getValue("name");
            if (name != null && !classes.isEmpty()) {
                Attribute attribute = new Attribute(name);
                ModelClass modelClass = classes.get(classes.size() - 1);
                if (modelClass != null) {
                    modelClass.getAttributes().add(attribute);
                }
            }
        }
        else if (pQName.equals("UML:Association")) {
            XMIRelation xmiRelation = new XMIRelation();
            xmiRelations.add(xmiRelation);
            String name = pAtts.getValue("name");
            if (name != null) {
                xmiRelation.setName(name);
            }
        }
        else if (pQName.equals("UML:Generalization")) {
            XMIRelation xmiRelation = new XMIRelation();
            String sourceID = pAtts.getValue("child");
            String targetID = pAtts.getValue("parent");
            if (sourceID == null) {
                sourceID = pAtts.getValue("subtype");
            }
            if (targetID == null) {
                targetID = pAtts.getValue("supertype");
            }
            String name = pAtts.getValue("name");
            if (name != null) {
                xmiRelation.setName(name);
            }
            if (sourceID != null) {
                xmiRelation.setSourceID(sourceID);
                xmiRelation.setTargetID(targetID);
                xmiRelation.setType(RelationType.GENERALIZATION);
                xmiRelations.add(xmiRelation);
            }
        }
        else if (pQName.equals("UML:Dependency")) {
            String name = pAtts.getValue("name");
            String idClient = pAtts.getValue("client");
            String idSupplier = pAtts.getValue("supplier");
            XMIRelation xmiRelation = new XMIRelation();
            xmiRelation.setType(RelationType.DEPENDENCY);
            xmiRelations.add(xmiRelation);
            if (name != null) {
                xmiRelation.setName(name);
            }
            if (idClient != null) {
                xmiRelation.setSourceID(idClient);
            }
            if (idSupplier != null) {
                xmiRelation.setTargetID(idSupplier);
            }
        }
        else if (pQName.equals("UML:Dependency.client")) {
            inDependencyClient = true;
        }

        else if (pQName.equals("UML:Dependency.supplier")) {
            inDependencySupplier = true;
        }
        else if (pQName.equals("Foundation.Core.ModelElement")) {
            if (xmiRelations.isEmpty())
                return;
            XMIRelation xmiRelation = xmiRelations.get(xmiRelations.size() - 1);
            String classID = pAtts.getValue("xmi.idref");

            if ((inDependencyClient) && (classID != null)) {
                xmiRelation.setSourceID(classID);
            }
            else if ((inDependencySupplier) && (classID != null)) {
                xmiRelation.setTargetID(classID);
            }
        }
        else if (pQName.equals("UML:TaggedValue")) {
            if (xmiRelations.isEmpty())
                return;
            XMIRelation xmiRelation = xmiRelations.get(xmiRelations.size() - 1);
            String tag = pAtts.getValue("tag");
            String direction = pAtts.getValue("value");
            if (tag != null && tag.equals("direction") && direction != null && direction.equals("Bi-Directional")) {
                xmiRelation.setType(RelationType.BIDIRECTED_ASSOCIATION);
            }
//			else if (tag != null && tag.equals("direction") && direction != null && direction.equals("Source -> Destination")) {
//				xmiRelation.setType(RelationType.DIRECTED_ASSOCIATION);
//			}
        }
        else if (pQName.equals("UML:AssociationEnd")) {
            if (xmiRelations.isEmpty())
                return;
            String name = pAtts.getValue("name");
            String multi = pAtts.getValue("multiplicity");
            String aggregation = pAtts.getValue("aggregation");
            String notDirected = pAtts.getValue("isNavigable");
            String classID = pAtts.getValue("type");
            XMIRelation xmiRelation = xmiRelations.get(xmiRelations.size() - 1);
            if (name != null) { // we draw it reversed
                if (source) {
                    xmiRelation.setTargetRoleName(name);
                }
                else {
                    xmiRelation.setSourceRoleName(name);
                }
            }
            if (multi != null) { // we draw it reversed
                if (source) {
                    if (multi.equals("1.."))
                        multi = "1..*";
                    xmiRelation.setTargetMultiplicity(multi);
                }
                else {
                    if (multi.equals("1.."))
                        multi = "1..*";
                    xmiRelation.setSourceMultiplicity(multi);
                }
            }
            if ((aggregation.equals("aggregate")) || (aggregation.equals("shared"))) {
                if (notDirected.equals("false")) {
                    xmiRelation.setType(RelationType.DIRECTED_AGGREGATION);
                }
                else {
                    xmiRelation.setType(RelationType.UNDIRECTED_AGGREGATION);
                }
            }
            else if (aggregation.equals("composite")) {
                if (notDirected.equals("false")) {
                    xmiRelation.setType(RelationType.DIRECTED_COMPOSITION);
                }
                else {
                    xmiRelation.setType(RelationType.UNDIRECTED_COMPOSITION);
                }
            }
//			else {
//				xmiRelation.setType(RelationType.UNDIRECTED_ASSOCIATION);
//			}
            if (classID != null) {
                if (source) {
                    xmiRelation.setTargetID(classID); // we draw it reversed
                }
                else {
                    xmiRelation.setSourceID(classID);
                }
            }
            changeSourceTarget();
        }
        else if (pQName.equals("UML:DiagramElement")) {
            String classID = pAtts.getValue("subject");
            String geometry = pAtts.getValue("geometry");
            if (classID != null) {
                for (String mappedID : idClassMap.keySet()) {
                    if (mappedID.equals(classID)) {
                        ModelClass modelClass = idClassMap.get(mappedID);
                        modelClass.setX(-getX(geometry) * 1.5 + 800);
                        modelClass.setZ(-getY(geometry) * 1.5 + 590);
                    }
                }
            }
        }
    }

    /**
     * Swap between source and target to set the line end to an association
     */
    private void changeSourceTarget() {
        if (source) {
            source = false;
        }
        else {
            source = true;
        }
    }

    /**
     * Extracts the x coordinate of the Geometry Tag. The Geometry String depends on the XMI Export Options Example 1:
     * geometry="Left=525;Top=441;Right=615;Bottom=511;" Example 2: geometry="2310,1666,315,245,"
     *
     * @param pGeometry
     * @return the x value
     */
    private double getX(String pGeometry) {
        double d = 0;
        String left = "";
        String regExp = "(Left\\=)(\\d*)\\;"; // Example 1
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(pGeometry);
        while (m.find()) {
            left = m.group(2);
        }
        if (!left.isEmpty()) {
            d = Double.valueOf(left);
        }
        else {
            regExp = "(\\d*)\\,(\\d*)\\,(\\d*)\\,(\\d*)"; // Example 2
            p = Pattern.compile(regExp);
            m = p.matcher(pGeometry);
            while (m.find()) {
                left = m.group(1);
            }
            if (!left.isEmpty()) {
                d = Double.valueOf(left);
                if (d > 500) {
                    scaling = true;
                }
                if (scaling) {
                    d = (d * 30) / 100;
                }
            }
        }
        return d;
    }

    /**
     * Extracts the y coordinate of the Geometry Tag. The Geometry String depends on the XMI Export Options Example 1:
     * geometry="Left=525;Top=441;Right=615;Bottom=511;" Example 2: geometry="2310,1666,315,245,"
     *
     * @param pGeometry
     * @return the y value
     */
    private double getY(String pGeometry) {
        double d = 0;
        String top = "";
        String regExp = "(Top\\=)(\\d*)\\;";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(pGeometry);
        while (m.find()) {
            top = m.group(2);
        }
        if (!top.isEmpty()) {
            d = Double.valueOf(top);
        }
        else {
            regExp = "(\\d*)\\,(\\d*)\\,(\\d*)\\,(\\d*)";
            p = Pattern.compile(regExp);
            m = p.matcher(pGeometry);
            while (m.find()) {
                top = m.group(2);
            }
            if (!top.isEmpty()) {
                d = Double.valueOf(top);
                if (d > 500) {
                    scaling = true;
                }
                if (scaling) {
                    d = (d * 30) / 100;
                }
            }
        }
        return d;
    }
}
