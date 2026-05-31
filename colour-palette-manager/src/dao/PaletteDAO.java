package dao;
import db.DatabaseConnection;
import model.Palette;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaletteDAO {
    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    public List<Palette> findAll() {
        List<Palette> list = new ArrayList<>();
        String sql = "SELECT * FROM palettes ORDER BY created_at DESC";
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Palette findById(int id) {
        String sql = "SELECT * FROM palettes WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /** Вставляет или обновляет в зависимости от id */
    public Palette save(Palette p) {
        return (p.getId() == 0) ? insert(p) : update(p);
    }

    private Palette insert(Palette p) {
        String sql = "INSERT INTO palettes(name, description, tags) VALUES(?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getDescription());
            ps.setString(3, p.getTags());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) p.setId(keys.getInt(1));
        } catch (SQLException e) { e.printStackTrace(); }
        return p;
    }

    private Palette update(Palette p) {
        String sql = "UPDATE palettes SET name=?, description=?, tags=? WHERE id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getDescription());
            ps.setString(3, p.getTags());
            ps.setInt(4, p.getId());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
        return p;
    }

    public void delete(int id) {
        String sql = "DELETE FROM palettes WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
    private Palette mapRow(ResultSet rs) throws SQLException {
        Palette p = new Palette();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setTags(rs.getString("tags"));
        return p;
    }
}
