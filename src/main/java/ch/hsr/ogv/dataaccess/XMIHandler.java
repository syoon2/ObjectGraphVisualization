package ch.hsr.ogv.dataaccess;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.helpers.DefaultHandler;

import ch.hsr.ogv.model.ModelClass;
import ch.hsr.ogv.model.Relation;

/**
 * This class is the Definition for a XMI Handler.
 *
 * @author Dario Vonaesch, Simon Gwerder
 * @version 3DCOV 3.0, May 2007 / OGV 3.1, May 2015
 */
public class XMIHandler extends DefaultHandler {

    protected List<XMIRelation> xmiRelations = new ArrayList<XMIRelation>();
    protected StringBuffer characters = new StringBuffer();
    protected Map<String, ModelClass> idClassMap = new LinkedHashMap<String, ModelClass>();

    /*
     * (non-Javadoc)
     *
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    public void characters(char[] pCH, int pStart, int pLength) {
        characters.append(pCH, pStart, pLength);
    }

    /**
     * Calls the mapping method and returns the class associations
     *
     * @return the read class associations
     */
    public List<Relation> getRelations() {
        return finalizeXMIRelations();
    }

    /**
     * Returns the read classes
     *
     * @return read classes
     */
    public List<ModelClass> getClasses() {
        return new ArrayList<ModelClass>(idClassMap.values());
    }

    /**
     * Maps the read classes to the class relations using the class id's
     */
    private ArrayList<Relation> finalizeXMIRelations() {
        ArrayList<Relation> relations = new ArrayList<Relation>();
        for (XMIRelation xmiRelation : xmiRelations) {
            ModelClass sourceClass = idClassMap.get(xmiRelation.getSourceID());
            ModelClass targetClass = idClassMap.get(xmiRelation.getTargetID());
            if (sourceClass != null && targetClass != null) {
                Relation relation = new Relation(sourceClass, targetClass, xmiRelation.getType());
                relations.add(relation);
                relation.setName(xmiRelation.getName());
                relation.getStart().setRoleName(xmiRelation.getSourceRoleName());
                relation.getStart().setMultiplicity(xmiRelation.getSourceMultiplicity());
                relation.getEnd().setRoleName(xmiRelation.getTargetRoleName());
                relation.getEnd().setMultiplicity(xmiRelation.getTargetMultiplicity());
            }
        }
        return relations;
    }

}