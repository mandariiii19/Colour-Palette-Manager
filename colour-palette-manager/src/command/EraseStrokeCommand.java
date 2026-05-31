package command;
import canvas.DrawingCanvas;
import canvas.Stroke;
public class EraseStrokeCommand  implements Command{
    private final DrawingCanvas canvas;
    private final Stroke        stroke;

    public EraseStrokeCommand(DrawingCanvas canvas, Stroke stroke) {
        this.canvas = canvas;
        this.stroke = stroke;
    }

    @Override public void execute()           { canvas.addStroke(stroke); }
    @Override public void undo()              { canvas.removeStroke(stroke); }
    @Override public String getDescription()  { return "Erase stroke"; }
}
