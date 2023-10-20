package ch.hsr.ogv.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javafx.scene.paint.Color;

import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import ch.hsr.ogv.dataaccess.ColorAdapter;

public class Relation {

    private String name = "";
    private Endpoint start;
    private Endpoint end;
    private RelationType relationType = RelationType.UNDIRECTED_ASSOCIATION;
    private Color color = Color.BLACK;

    /**
     * The property change support utility.
     * 
     * @since 4.0
     */
    private transient PropertyChangeSupport support = new PropertyChangeSupport(this);

    // for un/marshaling only
    public Relation() {
    }

    public Relation(ModelBox startBox, ModelBox endBox, RelationType relationType) {
        this(startBox, endBox, relationType, Color.BLACK);
    }

    public Relation(ModelBox startBox, ModelBox endBox, RelationType relationType, Color color) {
        this.start = new Endpoint(relationType.getStartType(), startBox);
        this.end = new Endpoint(relationType.getEndType(), endBox);
        this.relationType = relationType;
        this.start.setRelation(this);
        this.end.setRelation(this);
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        // setChanged();
        // notifyObservers(RelationChange.NAME);
        this.name = name;
    }

    public Endpoint getStart() {
        return start;
    }

    public void setStart(Endpoint start) {
        this.start = start;
    }

    public Endpoint getEnd() {
        return end;
    }

    public void setEnd(Endpoint end) {
        this.end = end;
    }

    public RelationType getRelationType() {
        return relationType;
    }

    public void setRelationType(RelationType type) {
        this.relationType = type;
    }

    @XmlJavaTypeAdapter(ColorAdapter.class)
    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        Color oldColor = this.color;
        this.color = color;
        support.firePropertyChange("color", oldColor, this.color);
    }

    public Endpoint getFriend(Endpoint endpoint) {
        if (endpoint.equals(start)) {
            return end;
        }
        return start;
    }

    public boolean isStart(Endpoint endpoint) {
        if (start != null && start.equals(endpoint)) {
            return true;
        }
        return false;
    }

    public boolean isEnd(Endpoint endpoint) {
        if (end != null && end.equals(endpoint)) {
            return true;
        }
        return false;
    }

    public boolean isReflexive() {
        if (start != null && end != null && start.getAppendant() != null && start.getAppendant().equals(end.getAppendant())) {
            return true;
        }
        return false;
    }

    public void changeDirection() {
        if (this.start == null || this.end == null)
            return;
        this.start.getAppendant().replaceEndpoint(this.start, this.end);
        this.end.getAppendant().replaceEndpoint(this.end, this.start);
        ModelBox tempModelBox = this.end.getAppendant();
        this.end.setAppendant(this.start.getAppendant());
        this.start.setAppendant(tempModelBox);
        support.firePropertyChange("direction", null, null);
    }

    public void setStartMultiplicity(String multiplicity) {
        if (this.start == null)
            return;
        String oldMultiplicity = this.start.getMultiplicity();
        this.start.setMultiplicity(multiplicity);
        support.firePropertyChange("multiplicity_role", oldMultiplicity, multiplicity);
    }

    public void setEndMultiplicity(String multiplicity) {
        if (this.end == null)
            return;
        String oldMultiplicity = this.end.getMultiplicity();
        this.end.setMultiplicity(multiplicity);
        support.firePropertyChange("multiplicity_role", oldMultiplicity, multiplicity);
    }

    public void setStartRoleName(String roleName) {
        if (this.start == null)
            return;
        
        String oldRoleName = this.start.getRoleName();
        this.start.setRoleName(roleName);
        support.firePropertyChange("multiplicity_role", oldRoleName, roleName);
    }

    public void setEndRoleName(String roleName) {
        if (this.end == null)
            return;
        String oldRoleName = this.end.getRoleName();
        this.end.setRoleName(roleName);
        support.firePropertyChange("multiplicity_role", oldRoleName, roleName);
    }

    /**
     * Adds a {@code PropertyChangeListener} to the listener list.
     * 
     * @param listener the {@code PropertyChangeListener} to be added
     * 
     * @since 4.0
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

}
