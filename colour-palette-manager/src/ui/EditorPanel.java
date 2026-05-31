package ui;
import adapter.ColorAdapter;
import canvas.CanvasToolbar;
import canvas.DrawingCanvas;
import command.*;
import facade.PaletteFacade;
import model.ColorModel;
import model.Palette;
import observer.PaletteObserver;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class EditorPanel extends JPanel implements PaletteObserver{
    private final PaletteFacade  facade;
    private final CommandManager cmdManager;
    private DrawingCanvas canvas;
    private CanvasToolbar canvasToolbar;
    private final ColorAdapter   adapter = new ColorAdapter();
    public void setCanvas(DrawingCanvas canvas, CanvasToolbar toolbar) {
        this.canvas        = canvas;
        this.canvasToolbar = toolbar;

        // When user right-clicks canvas and picks a color → highlight matching swatch
        canvas.setOnColorPickedCallback(hex -> SwingUtilities.invokeLater(() -> {
            highlightSwatchByHex(hex);
        }));
    }


    private Palette     current;
    private JLabel      titleLabel;
    private JLabel      metaLabel;
    private JPanel      swatchGrid;
    private JScrollPane scrollPane;

    public EditorPanel(PaletteFacade facade, CommandManager cmdManager) {
        this.facade     = facade;
        this.cmdManager = cmdManager;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        initComponents();
    }


    private void initComponents() {
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
                new EmptyBorder(14, 16, 12, 16)
        ));

        titleLabel = new JLabel("Select the palette on the left");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(new Color(40, 40, 40));

        metaLabel = new JLabel(" ");
        metaLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        metaLabel.setForeground(new Color(150, 150, 150));

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBox.setOpaque(false);
        titleBox.add(titleLabel);
        titleBox.add(metaLabel);
        header.add(titleBox, BorderLayout.CENTER);

        JButton delPaletteBtn = new JButton("Delete the palette");
        delPaletteBtn.setForeground(new Color(180, 40, 40));
        delPaletteBtn.addActionListener(e -> deleteCurrentPalette());
        header.add(delPaletteBtn, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        swatchGrid = new JPanel(new WrapLayout(FlowLayout.LEFT, 10, 10));
        swatchGrid.setBackground(Color.WHITE);
        swatchGrid.setBorder(new EmptyBorder(16, 16, 16, 16));

        scrollPane = new JScrollPane(swatchGrid);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        add(buildBottomBar(), BorderLayout.SOUTH);
    }

    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        bar.setBackground(new Color(250, 250, 250));
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0,
                new Color(220, 220, 220)));

        JButton addColorBtn = new JButton("+ Add colour");
        addColorBtn.addActionListener(e -> showAddColorDialog());

        JButton hexInputBtn = new JButton("Enter HEX");
        hexInputBtn.addActionListener(e -> showHexInputDialog());

        bar.add(addColorBtn);
        bar.add(hexInputBtn);
        return bar;
    }


    public void showPalette(Palette p) {
        this.current = p;
        refresh();
    }

    private void refresh() {
        if (current == null) return;
        titleLabel.setText(current.getName());

        String tags = current.getTags() == null || current.getTags().isBlank()
                ? "" : "  #" + current.getTags().replace(",", "  #");
        metaLabel.setText(current.getColors().size() + "colours " + tags);

        renderSwatches();
    }

    private void renderSwatches() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::renderSwatches);
            return;
        }

        swatchGrid.removeAll();
        swatchGrid.revalidate();
        swatchGrid.repaint();


        if (current != null && current.getColors() != null) {
            for (ColorModel c : current.getColors()) {
                swatchGrid.add(buildSwatchCard(c));
            }
        }

        JPanel addCard = new JPanel();
        addCard.setPreferredSize(new Dimension(90, 110));
        addCard.setLayout(new BorderLayout());
        addCard.setBorder(BorderFactory.createDashedBorder(
                new Color(180, 180, 200), 4, 4));
        addCard.setBackground(new Color(252, 252, 255));
        addCard.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel plus = new JLabel("+", SwingConstants.CENTER);
        plus.setFont(new Font("SansSerif", Font.PLAIN, 28));
        plus.setForeground(new Color(160, 160, 200));
        addCard.add(plus);

        addCard.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { showAddColorDialog(); }
            @Override public void mouseEntered(MouseEvent e) {
                addCard.setBackground(new Color(245, 244, 255));
            }
            @Override public void mouseExited(MouseEvent e) {
                addCard.setBackground(new Color(252, 252, 255));
            }
        });
        swatchGrid.add(addCard);


        SwingUtilities.invokeLater(() -> {
            swatchGrid.revalidate();
            swatchGrid.repaint();

            if (scrollPane != null && scrollPane.getViewport() != null) {
                scrollPane.getViewport().revalidate();
                scrollPane.getViewport().repaint();
            }
        });
    }

    private JPanel buildSwatchCard(ColorModel c) {
        JPanel card = new JPanel(new BorderLayout(0, 0));
        card.setPreferredSize(new Dimension(90, 110));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        final Color awtColor = c.toAwtColor();
        JPanel colorRect = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(awtColor);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        colorRect.setPreferredSize(new Dimension(90, 60));
        colorRect.setToolTipText(
                c.getHexValue() + "\n"
                        + facade.getRgbString(c.getHexValue()) + "\n"
                        + facade.getHslString(c.getHexValue())
        );
        colorRect.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && canvas != null) {
                    // Set this color as active brush color
                    canvas.setCurrentColor(c.toAwtColor());
                    if (canvasToolbar != null) canvasToolbar.updateColorDisplay();
                }
            }
        });

        card.add(colorRect, BorderLayout.CENTER);

        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setBackground(Color.WHITE);
        info.setBorder(new EmptyBorder(3, 5, 3, 5));

        JLabel hexLabel = new JLabel(c.getHexValue());
        hexLabel.setFont(new Font("Monospaced", Font.BOLD, 10));
        hexLabel.setForeground(new Color(60, 60, 60));

        JLabel nameLabel = new JLabel(truncate(c.getLabel(), 10));
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        nameLabel.setForeground(new Color(140, 140, 140));

        info.add(hexLabel);
        info.add(nameLabel);
        card.add(info, BorderLayout.SOUTH);

        JPopupMenu popup = new JPopupMenu();
        JMenuItem delItem = new JMenuItem("Delete colour");
        delItem.addActionListener(e -> {
            cmdManager.execute(new DeleteColorCommand(facade, c));
        });
        JMenuItem copyHex = new JMenuItem("Copy HEX");
        copyHex.addActionListener(e -> {
            Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(new java.awt.datatransfer.StringSelection(
                            c.getHexValue()), null);
        });
        popup.add(copyHex);
        popup.addSeparator();
        popup.add(delItem);
        card.setComponentPopupMenu(popup);
        colorRect.setComponentPopupMenu(popup);

        return card;
    }

    private void showAddColorDialog() {
        if (current == null) {
            JOptionPane.showMessageDialog(this, "First, select the palette.");
            return;
        }
        JColorChooser chooser = new JColorChooser(Color.RED);

        AbstractColorChooserPanel[] panels = chooser.getChooserPanels();
        for (AbstractColorChooserPanel p : panels) {
            if (!p.getDisplayName().equals("RGB") && !p.getDisplayName().equals("HSV"))
                chooser.removeChooserPanel(p);
        }

        int result = JOptionPane.showConfirmDialog(
                this, chooser, "Choose a colour",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );
        if (result == JOptionPane.OK_OPTION) {
            Color c   = chooser.getColor();
            String hex = adapter.rgbToHex(c.getRed(), c.getGreen(), c.getBlue());
            String label = JOptionPane.showInputDialog(
                    this, "Color name (optional):", hex
            );
            cmdManager.execute(new AddColorCommand(
                    facade, current.getId(), hex, label == null ? hex : label
            ));
        }
    }

    private void showHexInputDialog() {
        if (current == null) {
            JOptionPane.showMessageDialog(this, "First, select the palette.");
            return;
        }
        JTextField hexField   = new JTextField("#", 10);
        JTextField labelField = new JTextField(10);
        JLabel     preview    = new JLabel("         ");
        preview.setOpaque(true);
        preview.setBackground(Color.WHITE);
        preview.setBorder(BorderFactory.createLineBorder(Color.GRAY));


        hexField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            void update() {
                String txt = hexField.getText().trim();
                if (facade.isValidHex(txt)) {
                    try {
                        int[] rgb = adapter.hexToRgb(txt.startsWith("#") ? txt : "#" + txt);
                        preview.setBackground(new Color(rgb[0], rgb[1], rgb[2]));
                    } catch (Exception ignored) {}
                }
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { update(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { update(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
        });

        JPanel form = new JPanel(new GridLayout(5, 1, 4, 4));
        form.add(new JLabel("HEX (example #FF6B6B):"));
        form.add(hexField);
        form.add(new JLabel("Name:"));
        form.add(labelField);
        form.add(preview);

        int result = JOptionPane.showConfirmDialog(
                this, form, "Enter the HEX color",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );
        if (result == JOptionPane.OK_OPTION) {
            String hex = hexField.getText().trim();
            if (!hex.startsWith("#")) hex = "#" + hex;
            hex = hex.toUpperCase();
            if (facade.isValidHex(hex)) {
                String label = labelField.getText().trim();
                cmdManager.execute(new AddColorCommand(
                        facade, current.getId(), hex, label.isEmpty() ? hex : label
                ));
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid HEX format. Use the format #RRGGBB");
            }
        }
    }

    private void deleteCurrentPalette() {
        if (current == null) return;
        int ok = JOptionPane.showConfirmDialog(
                this,
                "Delete a palette «" + current.getName() + "» and all her colours?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (ok == JOptionPane.YES_OPTION) {
            cmdManager.execute(new DeletePaletteCommand(facade, current));
            current = null;
            titleLabel.setText("Select the palette on the left");
            metaLabel.setText(" ");
            swatchGrid.removeAll();
            swatchGrid.revalidate();
            swatchGrid.repaint();
        }
    }


    @Override
    public void onPaletteChanged(List<Palette> palettes) {
        SwingUtilities.invokeLater(() -> {
            if (current == null) return;
            palettes.stream()
                    .filter(p -> p.getId() == current.getId())
                    .findFirst()
                    .ifPresent(p -> {
                        current = p;
                        refresh();
                    });
        });
    }


    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }
    private void highlightSwatchByHex(String hex) {
        if (current == null) return;
        titleLabel.setToolTipText("Active brush: " + hex);
    }
}
