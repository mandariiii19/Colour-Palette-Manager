package command;
import facade.PaletteFacade;
import model.ColorModel;
import model.Palette;

import java.util.ArrayList;
import java.util.List;

public class DeletePaletteCommand implements Command{
    private final PaletteFacade    facade;
    private final Palette          snapshot;
    private final List<ColorModel> colorSnapshot;

    public DeletePaletteCommand(PaletteFacade facade, Palette palette) {
        this.facade        = facade;
        this.snapshot      = palette;
        this.colorSnapshot = new ArrayList<>(palette.getColors());
    }

    @Override
    public void execute() {
        facade.deletePalette(snapshot.getId());
    }

    @Override
    public void undo() {
        Palette restored = new Palette(snapshot.getName(),
                snapshot.getDescription(),
                snapshot.getTags());
        Palette saved = facade.savePalette(restored);
        for (ColorModel c : colorSnapshot) {
            facade.addColor(saved.getId(), c.getHexValue(), c.getLabel());
        }
    }

    @Override
    public String getDescription() {
        return "Delete palette «" + snapshot.getName() + "»";
    }
}
