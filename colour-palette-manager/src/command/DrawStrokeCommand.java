package command;
import canvas.DrawingCanvas;
import canvas.Stroke;

public class DrawStrokeCommand implements Command{
    private final DrawingCanvas canvas;
    private final Stroke        stroke;

    public DrawStrokeCommand(DrawingCanvas canvas, Stroke stroke) {
        this.canvas = canvas;
        this.stroke = stroke;
    }
    @Override public void execute()           { canvas.addStroke(stroke); }
    @Override public void undo()              { canvas.removeStroke(stroke); }
    @Override public String getDescription()  {
        return "Draw stroke (" + stroke.getPoints().size() + " points)";
    }
}
