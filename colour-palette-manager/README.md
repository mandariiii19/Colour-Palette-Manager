# 🎨 Colour Palette Manager

A full-stack desktop application for creating, organising, and reusing colour palettes — built with **Java Swing** and **MySQL**.

Designed for graphic designers, illustrators, and developers who need a persistent, structured way to manage colour collections across projects.

---

## ✨ Features

- **Palette CRUD** — create, rename, and delete named palettes stored in MySQL
- **Colour input** — add colours by HEX code (with live preview) or via the built-in JColorChooser
- **HEX / RGB / HSL display** — every swatch shows its HEX; tooltip shows RGB and HSL via ColorAdapter
- **Drawing canvas** — freehand painting with any palette colour as the active brush
- **Undo / Redo** — full history for both palette edits and canvas strokes (Ctrl+Z / Ctrl+Y)
- **Observer-driven UI** — any data change instantly refreshes sidebar, editor, and status bar

---

## 🏗️ Architecture

The application uses a **5-layer architecture** with **5 design patterns**:

```
UI (View)
    ↓
Facade / Service  ←→  Observer
    ↓
Command / Adapter
    ↓
DAO
    ↓
DB (Singleton)
```

| Layer | Package | Role |
|-------|---------|------|
| UI | `ui` | Swing panels; observes data via `PaletteObserver` |
| Canvas | `canvas` | `DrawingCanvas`, toolbar, stroke data |
| Facade | `facade` | `PaletteFacade` — single entry point for all data ops |
| Adapter | `adapter` | `ColorAdapter` — HEX ↔ RGB ↔ HSL conversion |
| Command | `command` | `CommandManager` + all concrete commands |
| Observer | `observer` | `PaletteObserver` interface |
| DAO | `dao` | `PaletteDAO`, `ColorDAO` — all SQL isolated here |
| Model | `model` | `Palette`, `ColorModel` POJOs |
| DB | `db` | `DatabaseConnection` Singleton — one JDBC connection |

### Design Patterns

| Pattern | Class | Purpose |
|---------|-------|---------|
| **Singleton** | `DatabaseConnection` | One JDBC connection for the entire app lifetime |
| **Adapter** | `ColorAdapter` | Converts between HEX, RGB, HSL without polluting UI |
| **Facade** | `PaletteFacade` | Shields UI from DAO/DB complexity |
| **Observer** | `PaletteObserver` | Auto-refreshes all UI panels on any data change |
| **Command** | `CommandManager` | Enables full undo/redo for palette edits and canvas strokes |

---

## 🗄️ Database Schema

```sql
palettes (id, name, tag, created_at)
colours  (id, palette_id FK, hex_value, red, green, blue, label, sort_order)
```

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- MySQL 8+
- IntelliJ IDEA (recommended) or any Java IDE

### Setup

1. **Clone the repo**
   ```bash
   git clone https://github.com/YOUR_USERNAME/colour-palette-manager.git
   cd colour-palette-manager
   ```

2. **Create the database**
   ```sql
   CREATE DATABASE palette_manager;
   ```
   Then run the schema SQL (see `docs/SRS_Colour_Palette_Manager.docx` for full schema).

3. **Configure the connection**  
   Edit `src/db/DatabaseConnection.java`:
   ```java
   private static final String URL  = "jdbc:mysql://localhost:3306/palette_manager";
   private static final String USER = "your_username";
   private static final String PASS = "your_password";
   ```

4. **Add the MySQL driver**  
   The connector JAR is already in `lib/mysql-connector-j-9.6.0.jar`. Make sure it's on your classpath.

5. **Run**  
   Execute `src/Main.java`.

---

## 📁 Project Structure

```
colour-palette-manager/
├── src/
│   ├── Main.java
│   ├── adapter/        ColorAdapter.java
│   ├── canvas/         DrawingCanvas.java, CanvasToolbar.java, Stroke.java
│   ├── command/        Command.java, CommandManager.java, Add/Delete/Draw commands
│   ├── dao/            PaletteDAO.java, ColorDAO.java
│   ├── db/             DatabaseConnection.java
│   ├── facade/         PaletteFacade.java
│   ├── model/          Palette.java, ColorModel.java
│   ├── observer/       PaletteObserver.java
│   └── ui/             MainFrame.java, EditorPanel.java, PaletteListPanel.java, StatusBar.java
├── lib/
│   └── mysql-connector-j-9.6.0.jar
├── docs/
│   ├── SRS_Colour_Palette_Manager.docx   ← Full software requirements specification
│   ├── Presentation_Colour_Palette_Manager.pptx
│   └── architecture.uml
├── demo/
│   └── demo.mp4                          ← Application walkthrough
└── README.md
```

---

## 🎬 Demo

See `demo/demo.mp4` for a full walkthrough of the application.

---

## 🛠️ Tech Stack

- **Language:** Java 17
- **UI:** Java Swing
- **Database:** MySQL 8 via JDBC
- **Driver:** mysql-connector-j 9.6.0
- **IDE:** IntelliJ IDEA

---

## 👩‍💻 Author

**Manaeva Daria**  
Dual-degree programme — Software Engineering  
Russian University (Top Universities of Russia & Moscow) + Neusoft Institute Guangdong, Dalian, China
