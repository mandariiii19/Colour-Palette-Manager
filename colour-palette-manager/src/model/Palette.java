package model;
import java.util.ArrayList;
import java.util.List;

public class Palette {
    private int    id;
    private String name;
    private String description;
    private String tags;
    private List<ColorModel> colors = new ArrayList<>();

    public Palette() {}

    public Palette(String name, String description, String tags) {
        this.name        = name;
        this.description = description == null ? "" : description;
        this.tags        = tags == null ? "" : tags;
    }

    public void addColor(ColorModel c)    { colors.add(c); }
    public void removeColor(ColorModel c) { colors.remove(c); }

    public int    getId()                       { return id; }
    public void   setId(int id)                 { this.id = id; }
    public String getName()                     { return name; }
    public void   setName(String n)             { this.name = n; }
    public String getDescription()              { return description; }
    public void   setDescription(String d)      { this.description = d == null ? "" : d; }
    public String getTags()                     { return tags; }
    public void   setTags(String t)             { this.tags = t == null ? "" : t; }
    public List<ColorModel> getColors()         { return colors; }
    public void   setColors(List<ColorModel> c) { this.colors = c; }

    @Override public String toString() { return name; }
}
