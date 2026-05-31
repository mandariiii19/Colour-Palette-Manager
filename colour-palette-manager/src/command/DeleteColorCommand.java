package command;
import facade.PaletteFacade;
import model.ColorModel;
public class DeleteColorCommand implements Command {
    private final PaletteFacade facade;
    private final ColorModel    snapshot;

    public DeleteColorCommand(PaletteFacade facade, ColorModel colorModel) {
        this.facade   = facade;
        this.snapshot = colorModel;
    }

    @Override
    public void execute() {
        facade.deleteColor(snapshot.getId());
    }

    @Override
    public void undo() {
        facade.addColor(snapshot.getPaletteId(),
                snapshot.getHexValue(),
                snapshot.getLabel());
    }

    @Override
    public String getDescription() {
        return "Удалить цвет " + snapshot.getHexValue();
    }
}
