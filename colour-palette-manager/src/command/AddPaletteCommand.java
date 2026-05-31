package command;
import facade.PaletteFacade;
import model.Palette;

public class AddPaletteCommand implements Command {
    private final PaletteFacade facade;
    private final String        name;
    private final String        description;
    private final String        tags;
    private Palette             saved;

    public AddPaletteCommand(PaletteFacade facade,
                             String name, String description, String tags) {
        this.facade      = facade;
        this.name        = name;
        this.description = description;
        this.tags        = tags;
    }

    @Override
    public void execute() {
        Palette p = new Palette(name, description, tags);
        saved = facade.savePalette(p);
    }

    @Override
    public void undo() {
        if (saved != null) {
            facade.deletePalette(saved.getId());
            saved = null;
        }
    }

    @Override
    public String getDescription() {
        return "Создать палитру «" + name + "»";
    }
}
