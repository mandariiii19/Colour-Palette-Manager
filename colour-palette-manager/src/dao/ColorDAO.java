package dao;
import db.DatabaseConnection;
import model.ColorModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ColorDAO {
    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    public List<ColorModel> findByPaletteId(int paletteId) {
        List<ColorModel> list = new ArrayList<>();
        String sql = "SELECT * FROM colors WHERE palette_id = ? ORDER BY position_order";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, paletteId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public ColorModel save(ColorModel c) {
        String sql = "INSERT INTO colors(palette_id, hex_value, r_value, g_value, "
                + "b_value, label, position_order) VALUES(?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, c.getPaletteId());
            ps.setString(2, c.getHexValue());
            ps.setInt(3, c.getR());
            ps.setInt(4, c.getG());
            ps.setInt(5, c.getB());
            ps.setString(6, c.getLabel());
            ps.setInt(7, c.getPosition());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) c.setId(keys.getInt(1));
        } catch (SQLException e) { e.printStackTrace(); }
        return c;
    }

    public void delete(int id) {
        String sql = "DELETE FROM colors WHERE id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deleteByPaletteId(int paletteId) {
        String sql = "DELETE FROM colors WHERE palette_id = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, paletteId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private ColorModel mapRow(ResultSet rs) throws SQLException {
        ColorModel c = new ColorModel();
        c.setId(rs.getInt("id"));
        c.setPaletteId(rs.getInt("palette_id"));
        c.setHexValue(rs.getString("hex_value"));
        c.setRgb(rs.getInt("r_value"), rs.getInt("g_value"), rs.getInt("b_value"));
        c.setLabel(rs.getString("label"));
        c.setPosition(rs.getInt("position_order"));
        return c;
    }
}
