import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ProductForm extends JFrame {
    private JTable productTable;

    public ProductForm() {
        setTitle("Manage Products");
        setSize(600, 400);
        setLayout(new BorderLayout());

        productTable = new JTable(new DefaultTableModel(new Object[]{"ID", "Name", "Category", "Price", "Quantity"}, 0));
        add(new JScrollPane(productTable), BorderLayout.CENTER);

        loadProducts();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void loadProducts() {
        String sql = "SELECT * FROM products";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            DefaultTableModel model = (DefaultTableModel) productTable.getModel();
            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{
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
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ProductForm::new);
    }
}
