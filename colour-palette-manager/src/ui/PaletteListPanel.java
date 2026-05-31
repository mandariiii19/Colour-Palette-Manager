package ui;
import command.AddPaletteCommand;
import command.CommandManager;
import facade.PaletteFacade;
import model.Palette;
import observer.PaletteObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;
public class PaletteListPanel extends JPanel implements PaletteObserver{
    private final PaletteFacade  facade;
    private final CommandManager cmdManager;
    private Consumer<Palette>    onSelectListener;

    private final JPanel listContainer;
    private       int    selectedId = -1;

    public PaletteListPanel(PaletteFacade facade, CommandManager cmdManager) {
        this.facade     = facade;
        this.cmdManager = cmdManager;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(230, 0));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1,
                new Color(220, 220, 220)));

        add(buildHeader(), BorderLayout.NORTH);

        listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(listContainer);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(new Color(248, 248, 248));
        h.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(8, 12, 8, 8)
        ));

        JLabel title = new JLabel("Palettes");
        title.setFont(new Font("SansSerif", Font.BOLD, 12));
        title.setForeground(new Color(100, 100, 100));
        h.add(title, BorderLayout.WEST);

        JButton addBtn = new JButton("+");
        addBtn.setToolTipText("Create a new palette");
        addBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        addBtn.setBorderPainted(false);
        addBtn.setContentAreaFilled(false);
        addBtn.setForeground(new Color(100, 100, 200));
        addBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addBtn.addActionListener(e -> showNewPaletteDialog());
        h.add(addBtn, BorderLayout.EAST);
        return h;
    }

    public void setOnSelectListener(Consumer<Palette> listener) {
        this.onSelectListener = listener;
    }


    @Override
    public void onPaletteChanged(List<Palette> palettes) {
        SwingUtilities.invokeLater(() -> rebuildList(palettes));
    }

    private void rebuildList(List<Palette> palettes) {
        listContainer.removeAll();
        listContainer.revalidate();
        listContainer.repaint();

        for (Palette p : palettes) {
            listContainer.add(buildRow(p));
        }

        listContainer.revalidate();
        listContainer.repaint();
    }

    private JPanel buildRow(Palette p) {
        boolean selected = (p.getId() == selectedId);

        JPanel row = new JPanel(new BorderLayout(0, 4));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));
        row.setBackground(selected ? new Color(238, 237, 254) : Color.WHITE);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(235, 235, 235)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel nameLabel = new JLabel(p.getName());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        nameLabel.setForeground(selected ? new Color(60, 52, 137) : new Color(40, 40, 40));
        row.add(nameLabel, BorderLayout.NORTH);

        JPanel swatches = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
        swatches.setOpaque(false);
        List<model.ColorModel> colors = p.getColors();
        int limit = Math.min(colors.size(), 8); // показываем не больше 8
        for (int i = 0; i < limit; i++) {
            final java.awt.Color awtColor = colors.get(i).toAwtColor();
            JPanel s = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(awtColor);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                }
            };
            s.setOpaque(false);
            s.setPreferredSize(new Dimension(16, 12));
            swatches.add(s);
        }
        if (colors.isEmpty()) {
            JLabel empty = new JLabel("no colours");
            empty.setFont(new Font("SansSerif", Font.PLAIN, 10));
            empty.setForeground(new Color(180, 180, 180));
            swatches.add(empty);
        }
        row.add(swatches, BorderLayout.CENTER);

        row.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                selectedId = p.getId();
                if (onSelectListener != null) onSelectListener.accept(p);
                rebuildList(facade.loadAll());
            }
            @Override public void mouseEntered(MouseEvent e) {
                if (p.getId() != selectedId)
                    row.setBackground(new Color(250, 249, 255));
            }
            @Override public void mouseExited(MouseEvent e) {
                if (p.getId() != selectedId)
                    row.setBackground(Color.WHITE);
            }
        });
        return row;
    }

    private void showNewPaletteDialog() {
        JTextField nameField = new JTextField(20);
        JTextField tagsField = new JTextField(20);

        JPanel form = new JPanel(new GridLayout(4, 1, 4, 4));
        form.add(new JLabel("Palette name:"));
        form.add(nameField);
        form.add(new JLabel("Tags (separated by commas):"));
        form.add(tagsField);

        int result = JOptionPane.showConfirmDialog(this, form,
                "New palette", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            if (!name.isEmpty()) {
                cmdManager.execute(new AddPaletteCommand(
                        facade, name, "", tagsField.getText().trim()
                ));
            }
        }
    }
}
