package facade;
import adapter.ColorAdapter;
import dao.ColorDAO;
import dao.PaletteDAO;
import model.ColorModel;
import model.Palette;
import observer.PaletteObserver;

import java.util.ArrayList;
import java.util.List;
public class PaletteFacade {
    private final PaletteDAO   paletteDAO = new PaletteDAO();
    private final ColorDAO     colorDAO   = new ColorDAO();
    private final ColorAdapter adapter    = new ColorAdapter();
    private final List<PaletteObserver> observers = new ArrayList<>();
    public void addObserver(PaletteObserver o)    { observers.add(o); }

    public void notifyObservers() {
        List<Palette> all = loadAll();
        for (PaletteObserver o : observers) {
            o.onPaletteChanged(all);
        }
    }

    public List<Palette> loadAll() {
        List<Palette> palettes = paletteDAO.findAll();
        for (Palette p : palettes) {
            List<ColorModel> colors = colorDAO.findByPaletteId(p.getId());
            p.setColors(colors);
        }
        return palettes;
    }

    public Palette savePalette(Palette p) {
        Palette saved = paletteDAO.save(p);
        notifyObservers();
        return saved;
    }

    public void deletePalette(int paletteId) {
        paletteDAO.delete(paletteId);
        notifyObservers();
    }

    public ColorModel addColor(int paletteId, String hex, String label) {
        ColorModel c = adapter.hexToColorModel(hex, label);
        c.setPaletteId(paletteId);

        List<ColorModel> existing = colorDAO.findByPaletteId(paletteId);
        c.setPosition(existing.size());

        ColorModel saved = colorDAO.save(c);
        notifyObservers();
        return saved;
    }

    public void deleteColor(int colorId) {
        colorDAO.delete(colorId);
        notifyObservers();
    }

    public String getRgbString(String hex) {
        int[] rgb = adapter.hexToRgb(hex);
        return adapter.toRgbString(rgb[0], rgb[1], rgb[2]);
    }

    public String getHslString(String hex) {
        return adapter.toHslString(hex);
    }

    public boolean isValidHex(String hex) {
        return hex != null && hex.matches("^#?[0-9A-Fa-f]{6}$");
    }
}
