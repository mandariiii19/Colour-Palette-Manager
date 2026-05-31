package model;
import java.awt.Color;
public class ColorModel {
    private int    id;
    private int    paletteId;
    private String hexValue;
    private int    r, g, b;
    private String label;
    private int    position;

    public ColorModel() {}

    public ColorModel(String hexValue, String label) {
        this.hexValue = hexValue;
        this.label    = (label == null || label.isBlank()) ? hexValue : label;
        parseHex(hexValue);
    }

    private void parseHex(String hex) {
        String h = hex.startsWith("#") ? hex.substring(1) : hex;
        this.r = Integer.parseInt(h.substring(0, 2), 16);
        this.g = Integer.parseInt(h.substring(2, 4), 16);
        this.b = Integer.parseInt(h.substring(4, 6), 16);
    }

    public Color toAwtColor() { return new Color(r, g, b); }

    public int    getId()                      { return id; }
    public void   setId(int id)                { this.id = id; }
    public int    getPaletteId()               { return paletteId; }
    public void   setPaletteId(int pid)        { this.paletteId = pid; }
    public String getHexValue()                { return hexValue; }
    public void   setHexValue(String h)        { this.hexValue = h; parseHex(h); }
    public int    getR()                       { return r; }
    public int    getG()                       { return g; }
    public int    getB()                       { return b; }
    public void   setRgb(int r, int g, int b)  { this.r=r; this.g=g; this.b=b; }
    public String getLabel()                   { return label; }
    public void   setLabel(String l)           { this.label = l; }
    public int    getPosition()                { return position; }
    public void   setPosition(int p)           { this.position = p; }

    @Override public String toString() { return label + " (" + hexValue + ")"; }
}
