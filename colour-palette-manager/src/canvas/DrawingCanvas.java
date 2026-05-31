package canvas;
import adapter.ColorAdapter;
import command.CommandManager;
import command.DrawStrokeCommand;
import command.EraseStrokeCommand;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DrawingCanvas extends JPanel{
    private final List<Stroke> strokes     = new ArrayList<>();
    private       Stroke       currentStroke;

    private       Color        currentColor = new Color(60, 60, 60);
    private       int          brushSize    = 6;
    private       boolean      eraseMode    = false;

    private final CommandManager         cmdManager;
    private final ColorAdapter           adapter;
    private       Consumer<String>       onColorPickedCallback; // hex → EditorPanel

    // ── Constructor ─────────────────────────────────────────────────────────
    public DrawingCanvas(CommandManager cmdManager) {
        this.cmdManager = cmdManager;
        this.adapter    = new ColorAdapter();
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)));
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

        attachMouseListeners();
    }

    // ── Mouse Listeners ──────────────────────────────────────────────────────
    private void attachMouseListeners() {
        MouseAdapter ma = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    // Right-click: pick color from canvas pixel
                    pickColorAt(e.getPoint());
                    return;
                }
                // Start new stroke
                currentStroke = new Stroke(
                        eraseMode ? Color.WHITE : currentColor,
                        eraseMode ? brushSize * 3 : brushSize,
                        eraseMode
                );
                currentStroke.addPoint(e.getPoint());
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (currentStroke == null) return;
                currentStroke.addPoint(e.getPoint());
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (currentStroke == null || currentStroke.isEmpty()) return;

                // Wrap the finished stroke in a Command and execute it
                // This registers it on the undo stack
                if (eraseMode) {
                    cmdManager.execute(new EraseStrokeCommand(
                            DrawingCanvas.this, currentStroke
                    ));
                } else {
                    cmdManager.execute(new DrawStrokeCommand(
                            DrawingCanvas.this, currentStroke
                    ));
                }
                currentStroke = null;
            }
        };

        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    // ── Painting ─────────────────────────────────────────────────────────────
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE);

        // Draw committed strokes
        for (Stroke s : strokes) {
            drawStroke(g2, s);
        }

        // Draw the stroke being made right now (before mouseReleased)
        if (currentStroke != null) {
            drawStroke(g2, currentStroke);
        }
    }

    private void drawStroke(Graphics2D g2, Stroke s) {
        List<Point> pts = s.getPoints();
        if (pts.isEmpty()) return;

        g2.setColor(s.getColor());
        g2.setStroke(new BasicStroke(
                s.getWidth(),
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND
        ));

        if (pts.size() == 1) {
            // Single dot
            Point p = pts.get(0);
            int r = s.getWidth() / 2;
            g2.fillOval(p.x - r, p.y - r, s.getWidth(), s.getWidth());
            return;
        }

        for (int i = 1; i < pts.size(); i++) {
            Point a = pts.get(i - 1);
            Point b = pts.get(i);
            g2.drawLine(a.x, a.y, b.x, b.y);
        }
    }

    // ── Color picking ────────────────────────────────────────────────────────
    /**
     * Right-click: read the pixel color at that canvas coordinate,
     * convert to HEX via ColorAdapter, and notify the EditorPanel
     * so the active brush color + swatch selection update.
     */
    private void pickColorAt(Point p) {
        try {
            // Render the canvas to a BufferedImage to sample the pixel
            java.awt.image.BufferedImage img =
                    new java.awt.image.BufferedImage(
                            Math.max(getWidth(), 1),
                            Math.max(getHeight(), 1),
                            java.awt.image.BufferedImage.TYPE_INT_RGB
                    );
            Graphics2D ig = img.createGraphics();
            paintComponent(ig);
            ig.dispose();

            int px = Math.max(0, Math.min(p.x, img.getWidth()  - 1));
            int py = Math.max(0, Math.min(p.y, img.getHeight() - 1));
            Color picked = new Color(img.getRGB(px, py));
            String hex   = adapter.rgbToHex(
                    picked.getRed(), picked.getGreen(), picked.getBlue()
            );

            // Update brush color immediately
            this.currentColor = picked;

            // Notify listener (EditorPanel will highlight the matching swatch)
            if (onColorPickedCallback != null) {
                onColorPickedCallback.accept(hex);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ── Public API (used by Commands and EditorPanel) ─────────────────────────

    /** Called by DrawStrokeCommand.execute() and EraseStrokeCommand.execute() */
    public void addStroke(Stroke s) {
        strokes.add(s);
        repaint();
    }

    /** Called by DrawStrokeCommand.undo() and EraseStrokeCommand.undo() */
    public void removeStroke(Stroke s) {
        strokes.remove(s);
        repaint();
    }

    /** Returns a snapshot for ClearCanvasCommand */
    public List<Stroke> getStrokes() {
        return new ArrayList<>(strokes);
    }

    /** Called by ClearCanvasCommand.undo() to restore all strokes */
    public void restoreStrokes(List<Stroke> saved) {
        strokes.clear();
        strokes.addAll(saved);
        repaint();
    }

    public void clearAll() {
        strokes.clear();
        repaint();
    }

    // ── Setters called from CanvasToolbar ────────────────────────────────────
    public void setCurrentColor(Color c)        { this.currentColor = c; }
    public Color getCurrentColor()              { return currentColor; }
    public void setEraseMode(boolean erase)     { this.eraseMode = erase; }
    public boolean isEraseMode()                { return eraseMode; }
    public void setBrushSize(int size)          { this.brushSize = size; }
    public int  getBrushSize()                  { return brushSize; }
    public void setOnColorPickedCallback(Consumer<String> cb) {
        this.onColorPickedCallback = cb;
    }
}
