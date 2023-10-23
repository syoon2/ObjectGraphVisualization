package ch.hsr.ogv.dataaccess;

import java.util.Objects;

import javafx.scene.paint.Color;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class ColorAdapter extends XmlAdapter<String, Color> {

    /**
     * Converts a color to its CSS representation.
     * 
     * @param color the color to be converted
     * @return the CSS representation of the specified color
     * 
     * @throws NullPointerException if argument is {@code null}
     * 
     * @since 4.0
     */
    public static String toCSSColor(Color color) {
        Objects.requireNonNull(color);
        StringBuilder sb = new StringBuilder("rgba(");
        sb.append(Math.round(color.getRed() * 255));
        sb.append(", ");
        sb.append(Math.round(color.getGreen() * 255));
        sb.append(", ");
        sb.append(Math.round(color.getBlue() * 255));
        sb.append(", ");
        sb.append(color.getOpacity());
        sb.append(");");
        return sb.toString();
    }

    /*
     * Java => XML
     */
    @Override
    public String marshal(Color val) {
        return toCSSColor(val);
    }

    /*
     * XML => Java
     */
    @Override
    public Color unmarshal(String val) {
        return Color.web(val);
    }
}
