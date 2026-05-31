package command;

import facade.PaletteFacade;
import model.ColorModel;

public class AddColorCommand implements Command{
    private final PaletteFacade facade;
    private final int           paletteId;
    private final String        hex;
    private final String        label;
    private ColorModel          saved; // запоминаем чтобы удалить при undo

    public AddColorCommand(PaletteFacade facade, int paletteId, String hex, String label) {
        this.facade    = facade;
        this.paletteId = paletteId;
        this.hex       = hex;
        this.label     = label;
    }

    @Override
    public void execute() {
        saved = facade.addColor(paletteId, hex, label);
    }

    @Override
    public void undo() {
        if (saved != null) {
            facade.deleteColor(saved.getId());
            saved = null;
        }
    }

    @Override
    public String getDescription() {
        return "Добавить цвет " + hex + " («" + label + "»)";
    }
}
