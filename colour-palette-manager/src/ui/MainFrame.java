package ui;
import canvas.CanvasToolbar;
import canvas.DrawingCanvas;
import command.CommandManager;
import facade.PaletteFacade;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
public class MainFrame extends JFrame {
    private final PaletteFacade facade = new PaletteFacade();
    private final CommandManager cmdManager = new CommandManager();

    public MainFrame(){
        super("Color Palette Manager");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(1050, 680);
        setMinimumSize(new Dimension(750, 480));

        initUI();
        setLocationRelativeTo(null);
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                db.DatabaseConnection.getInstance().close();
                dispose();
                System.exit(0);
            }
        });
    }

    private void initUI() {
        setLayout(new BorderLayout());
        add(buildToolbar(), BorderLayout.NORTH);

        // ── Left: palette list ──────────────────────────────────────────────────
        PaletteListPanel listPanel = new PaletteListPanel(facade, cmdManager);

        // ── Center: editor (swatches) above, canvas below ───────────────────────
        EditorPanel   editorPanel = new EditorPanel(facade, cmdManager);
        DrawingCanvas canvas      = new DrawingCanvas(cmdManager);
        CanvasToolbar canvasTb    = new CanvasToolbar(canvas, cmdManager);

        // Wire canvas ↔ editor: clicking a swatch sets the brush color
        editorPanel.setCanvas(canvas, canvasTb);

        // Canvas panel = toolbar + canvas
        JPanel canvasWrapper = new JPanel(new BorderLayout());
        canvasWrapper.add(canvasTb, BorderLayout.NORTH);
        canvasWrapper.add(canvas,   BorderLayout.CENTER);
        canvasWrapper.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                "Drawing canvas"
        ));

        // Editor fills top ~35%, canvas fills bottom ~65%
        JSplitPane rightSplit = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT, editorPanel, canvasWrapper
        );
        rightSplit.setDividerLocation(260);
        rightSplit.setDividerSize(4);
        rightSplit.setBorder(null);

        // Main split: list | right
        JSplitPane mainSplit = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, listPanel, rightSplit
        );
        mainSplit.setDividerLocation(230);
        mainSplit.setDividerSize(1);
        mainSplit.setBorder(null);

        add(mainSplit, BorderLayout.CENTER);

        // ── Observers ────────────────────────────────────────────────────────────
        StatusBar statusBar = new StatusBar();
        facade.addObserver(listPanel);
        facade.addObserver(editorPanel);
        facade.addObserver(statusBar);
        add(statusBar, BorderLayout.SOUTH);

        listPanel.setOnSelectListener(editorPanel::showPalette);

        facade.notifyObservers();
    }

    private JToolBar buildToolbar() {
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);
        tb.setBackground(new Color(250, 250, 250));
        tb.setBorder(BorderFactory.createMatteBorder(
                0, 0, 1, 0, new Color(215, 215, 215)
        ));

        JButton undoBtn = new JButton("↩ Cancel");
        JButton redoBtn = new JButton("↪ Repeat");

        undoBtn.addActionListener(e -> {
            cmdManager.undo();
            updateToolbarButtons(undoBtn, redoBtn);
        });
        redoBtn.addActionListener(e -> {
            cmdManager.redo();
            updateToolbarButtons(undoBtn, redoBtn);
        });

        facade.addObserver(palettes -> SwingUtilities.invokeLater(
                () -> updateToolbarButtons(undoBtn, redoBtn)
        ));

        updateToolbarButtons(undoBtn, redoBtn);

        tb.addSeparator(new Dimension(8, 0));
        tb.add(undoBtn);
        tb.add(redoBtn);
        tb.addSeparator();

        JLabel hint = new JLabel("Right-click on the swatch to copy HEX or delete the color");
        hint.setFont(new Font("SansSerif", Font.PLAIN, 11));
        hint.setForeground(new Color(160, 160, 160));
        tb.add(hint);

        return tb;
    }

    private void updateToolbarButtons(JButton undo, JButton redo) {
        undo.setEnabled(cmdManager.canUndo());
        redo.setEnabled(cmdManager.canRedo());
        undo.setToolTipText(cmdManager.canUndo()
                ? "Cancel: " + cmdManager.getUndoDescription() : "There's nothing to cancel");
        redo.setToolTipText(cmdManager.canRedo()
                ? "Repeat: " + cmdManager.getRedoDescription() : "There's nothing to cancel");
    }

}
