package command;
import canvas.DrawingCanvas;
import canvas.Stroke;
import java.util.List;

public class ClearCanvasCommand implements Command {
    private final DrawingCanvas canvas;
    private List<Stroke>        snapshot; // saved before clear

    public ClearCanvasCommand(DrawingCanvas canvas) {
        this.canvas = canvas;
    }

    @Override
    public void execute() {
        snapshot = canvas.getStrokes();
        canvas.clearAll();
    }
    @Override
    public void undo() {
        if (snapshot != null) {
            canvas.restoreStrokes(snapshot);
        }
    }

    @Override
    public String getDescription() { return "Clear canvas"; }
}
