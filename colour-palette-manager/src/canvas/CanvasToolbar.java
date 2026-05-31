package canvas;

import command.ClearCanvasCommand;
import command.CommandManager;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;

public class CanvasToolbar extends JPanel {
    private final DrawingCanvas  canvas;
    private final CommandManager cmdManager;

    private final JButton    drawBtn   = new JButton("Draw");
    private final JButton    eraseBtn  = new JButton("Erase");
    private final JSlider    sizeSlider;
    private final JLabel     sizeLabel = new JLabel("6px");
    private final JPanel     colorSwatch;


    public CanvasToolbar(DrawingCanvas canvas, CommandManager cmdManager) {
        this.canvas     = canvas;
        this.cmdManager = cmdManager;

        setLayout(new FlowLayout(FlowLayout.LEFT, 8, 6));
        setBackground(new Color(248, 248, 248));
        setBorder(BorderFactory.createMatteBorder(
                0, 0, 1, 0, new Color(215, 215, 215)
        ));

        // ── Draw / Erase toggle ─────────────────────────────────────────────
        styleToggleBtn(drawBtn,  true);
        styleToggleBtn(eraseBtn, false);

        drawBtn.addActionListener(e -> {
            canvas.setEraseMode(false);
            styleToggleBtn(drawBtn,  true);
            styleToggleBtn(eraseBtn, false);
        });
        eraseBtn.addActionListener(e -> {
            canvas.setEraseMode(true);
            styleToggleBtn(drawBtn,  false);
            styleToggleBtn(eraseBtn, true);
        });

        // ── Brush size slider ───────────────────────────────────────────────
        sizeSlider = new JSlider(1, 40, 6);
        sizeSlider.setPreferredSize(new Dimension(100, 24));
        sizeSlider.setOpaque(false);
        sizeSlider.addChangeListener((ChangeEvent e) -> {
            int v = sizeSlider.getValue();
            canvas.setBrushSize(v);
            sizeLabel.setText(v + "px");
        });

        // ── Active color swatch ─────────────────────────────────────────────
        colorSwatch = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(canvas.getCurrentColor());
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g.setColor(new Color(180, 180, 180));
                ((Graphics2D)g).draw(new java.awt.geom.RoundRectangle2D.Float(
                        0.5f, 0.5f, getWidth()-1, getHeight()-1, 6, 6
                ));
            }
        };
        colorSwatch.setPreferredSize(new Dimension(28, 28));
        colorSwatch.setOpaque(false);
        colorSwatch.setToolTipText("Active color — right-click on canvas to pick");

        // ── Clear button ────────────────────────────────────────────────────
        JButton clearBtn = new JButton("Clear");
        clearBtn.setForeground(new Color(160, 60, 60));
        clearBtn.addActionListener(e -> {
            int ok = JOptionPane.showConfirmDialog(
                    this, "Clear the entire canvas?",
                    "Confirm", JOptionPane.YES_NO_OPTION
            );
            if (ok == JOptionPane.YES_OPTION) {
                cmdManager.execute(new ClearCanvasCommand(canvas));
            }
        });

        // ── Hint ─────────────────────────────────────────────────────────────
        JLabel hint = new JLabel("Right-click = pick color from canvas");
        hint.setFont(new Font("SansSerif", Font.PLAIN, 11));
        hint.setForeground(new Color(160, 160, 160));

        // ── Layout ───────────────────────────────────────────────────────────
        add(drawBtn);
        add(eraseBtn);
        add(new JSeparator(JSeparator.VERTICAL));
        add(new JLabel("Size:"));
        add(sizeSlider);
        add(sizeLabel);
        add(new JSeparator(JSeparator.VERTICAL));
        add(new JLabel("Color:"));
        add(colorSwatch);
        add(new JSeparator(JSeparator.VERTICAL));
        add(clearBtn);
        add(hint);
    }

    /** Called from EditorPanel when user clicks a palette swatch */
    public void updateColorDisplay() {
        colorSwatch.repaint();
    }

    private void styleToggleBtn(JButton btn, boolean active) {
        if (active) {
            btn.setBackground(new Color(127, 119, 221));
            btn.setForeground(Color.WHITE);
            btn.setOpaque(true);
        } else {
            btn.setBackground(null);
            btn.setForeground(new Color(80, 80, 80));
            btn.setOpaque(false);
        }
        btn.repaint();
    }
}
