package canvas;
import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Stroke {
    private final List<Point> points    = new ArrayList<>();
    private final Color       color;
    private final int         width;
    private final boolean     isEraser;

    public Stroke(Color color, int width, boolean isEraser) {
        this.color    = color;
        this.width    = width;
        this.isEraser = isEraser;
    }

    public void addPoint(Point p)        { points.add(p); }
    public List<Point> getPoints()       { return points; }
    public Color       getColor()        { return color; }
    public int         getWidth()        { return width; }
    public boolean     isEraser()        { return isEraser; }
    public boolean     isEmpty()         { return points.isEmpty(); }
}
