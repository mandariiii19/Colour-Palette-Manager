package observer;
import model.Palette;
import java.util.List;

public interface PaletteObserver {
    void onPaletteChanged(List<Palette> palettes);
}
