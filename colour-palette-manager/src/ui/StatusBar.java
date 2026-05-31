package ui;
import model.Palette;
import observer.PaletteObserver;

import javax.swing.*;
import java.awt.*;
import java.util.List;
public class StatusBar extends JPanel implements PaletteObserver{
    private final JLabel infoLabel = new JLabel("Loading...");

    public StatusBar() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 12, 4));
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(210, 210, 210)));
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        infoLabel.setForeground(new Color(120, 120, 120));
        add(infoLabel);
    }

    @Override
    public void onPaletteChanged(List<Palette> palettes) {
        int totalColors = palettes.stream()
                .mapToInt(p -> p.getColors().size())
                .sum();
        SwingUtilities.invokeLater(() ->
                infoLabel.setText("Palettes: " + palettes.size()
                        + "   |   Total colors: " + totalColors
                        + "   |   MySQL · connected")
        );
    }
}
