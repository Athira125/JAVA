import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    public ProductDAO() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS products (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "category TEXT," +
                "price REAL," +
                "quantity INTEGER)";
        try (Statement stmt = DBConnection.getConnection().createStatement()) {
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public List<Object[]> getAllProducts() {
        List<Object[]> products = new ArrayList<>();
        String sql = "SELECT * FROM products";
        try (Statement stmt = DBConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                products.add(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getInt("quantity")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return products;
    }

    public void addProduct(String name, String category, double price, int quantity) {
        String sql = "INSERT INTO products (name, category, price, quantity) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = DBConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, category);
            pstmt.setDouble(3, price);
            pstmt.setInt(4, quantity);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void updateProduct(int id, String name, String category, double price, int quantity) {
        String sql = "UPDATE products SET name=?, category=?, price=?, quantity=? WHERE id=?";
        try (PreparedStatement pstmt = DBConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, category);
            pstmt.setDouble(3, price);
            pstmt.setInt(4, quantity);
            pstmt.setInt(5, id);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void deleteProduct(int id) {
        String sql = "DELETE FROM products WHERE id=?";
        try (PreparedStatement pstmt = DBConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
