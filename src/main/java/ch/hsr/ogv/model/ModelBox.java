package ch.hsr.ogv.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import ch.hsr.ogv.dataaccess.ColorAdapter;
import ch.hsr.ogv.dataaccess.Point3DAdapter;

@XmlType(propOrder = {"name", "coordinates", "width", "height", "color", "endpoints"})
public abstract class ModelBox {

    protected String name = "";
    protected Point3D coordinates = new Point3D(0, 0, 0);
    protected double width = 100.0;
    protected double height = 100.0;
    protected Color color = Color.CORNSILK;
    protected List<Endpoint> endpoints = new ArrayList<Endpoint>();

    /**
     * The property change support utility.
     * 
     * @since 4.0
     */
    protected transient PropertyChangeSupport support = new PropertyChangeSupport(this);

    // for un/marshaling only
    public ModelBox() {
    }

    public ModelBox(String name, Point3D coordinates, double width, double height, Color color) {
        this.name = name;
        this.coordinates = coordinates;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        // not doing the equals check here for when ModelObject needs a name change event
        if (this.name != null) {
            String oldName = this.name;
            this.name = name;
            support.firePropertyChange("name", oldName, name);
        }
    }

    @XmlJavaTypeAdapter(Point3DAdapter.class)
    public Point3D getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Point3D coordinates) {
        if (this.coordinates != null) {
            Point3D oldCoordinates = this.coordinates;
            this.coordinates = coordinates;
            support.firePropertyChange("coordinates", oldCoordinates, coordinates);
        }
    }

    public void setX(double x) {
        Point3D coords = new Point3D(x, 0, 0);
        if (coordinates != null) {
            coords = new Point3D(x, this.coordinates.getY(), this.coordinates.getZ());
        }
        setCoordinates(coords);
    }

    public void setY(double y) {
        Point3D coords = new Point3D(0, y, 0);
        if (coordinates != null) {
            coords = new Point3D(this.coordinates.getX(), y, this.coordinates.getZ());
        }
        setCoordinates(coords);
    }

    public void setZ(double z) {
        Point3D coords = new Point3D(0, 0, z);
        if (coordinates != null) {
            coords = new Point3D(this.coordinates.getX(), this.coordinates.getY(), z);
        }
        setCoordinates(coords);
    }

    @XmlTransient
    public double getX() {
        return this.coordinates.getX();
    }

    @XmlTransient
    public double getY() {
        return this.coordinates.getY();
    }

    @XmlTransient
    public double getZ() {
        return this.coordinates.getZ();
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        if (this.width != width) {
            double oldWidth = this.width;
            this.width = width;
            support.firePropertyChange("width", Double.valueOf(oldWidth), Double.valueOf(width));
        }
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        if (this.height != height) {
            double oldHeight = this.height;
            this.height = height;
            support.firePropertyChange("height", Double.valueOf(oldHeight), Double.valueOf(height));
        }
    }

    @XmlJavaTypeAdapter(ColorAdapter.class)
    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        if (this.color != null && !this.color.equals(color)) {
            Color oldColor = this.color;
            this.color = color;
            support.firePropertyChange("color", oldColor, color);
        }
    }

    @XmlElementWrapper(name = "endpoints")
    @XmlElement(name = "endpoint")
    public List<Endpoint> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<Endpoint> endpoints) {
        this.endpoints = endpoints;
    }

    @XmlTransient
    public Map<Endpoint, Endpoint> getFriends() {
        Map<Endpoint, Endpoint> result = new LinkedHashMap<Endpoint, Endpoint>(endpoints.size());
        for (Endpoint endpoint : endpoints) {
            Endpoint friend = endpoint.getFriend();
            if (friend != null) {
                result.put(endpoint, endpoint.getFriend());
            }
        }
        return result;
    }

    public boolean replaceEndpoint(Endpoint toReplace, Endpoint replacement) {
        int index = this.endpoints.indexOf(toReplace);
        if (index >= 0) {
            this.endpoints.set(index, replacement);
            return true;
        }
        return false;
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
