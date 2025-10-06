 import javax.swing.*;
 import java.awt.event.*;
 import java.sql.*;
 import java.awt.GridBagLayout;
 import java.awt.GridBagConstraints;
 import java.awt.Insets;

 public class LoginForm extends JFrame {
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnLogin;
    private JButton btnRegister;
    public LoginForm() {
        setTitle("Furniture Shop - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtUser = new JTextField(15);
        add(txtUser, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtPass = new JPasswordField(15);
        add(txtPass, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        
        JPanel buttonPanel = new JPanel();
        btnLogin = new JButton("Login");
        btnRegister = new JButton("Register");
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnRegister);
        add(buttonPanel, gbc);

        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        btnRegister.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                register();
            }
        });

        createUsersTableIfNotExists();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    private void login() {
        String user = txtUser.getText();
        String pass = new String(txtPass.getPassword());
        try {
            String sql = "SELECT password FROM users WHERE username=?";
            PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql);
            stmt.setString(1, user);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedHash = rs.getString("password");
                if (BCrypt.checkpw(pass, storedHash)) {
                    JOptionPane.showMessageDialog(this, "Login successful");
                    this.dispose();
                    new ProductForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }
    private void register() {
        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();
        Object[] fields = {
            "Username:", userField,
            "Password:", passField
        };
        int result = JOptionPane.showConfirmDialog(this, fields, "Register", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username and password cannot be empty.");
                return;
            }
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
            try (PreparedStatement pstmt = DBConnection.getConnection().prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, hashedPassword);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Account created successfully!");
            } catch (SQLException ex) {
                if (ex.getMessage().contains("UNIQUE")) {
                    JOptionPane.showMessageDialog(this, "Username already exists.");
                } else {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error creating account: " + ex.getMessage());
                }
            }
        }
    }

    private void createUsersTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT NOT NULL UNIQUE," +
                "password TEXT NOT NULL)";
        try (Statement stmt = DBConnection.getConnection().createStatement()) {
            stmt.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error creating users table: " + ex.getMessage());
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginForm::new);
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                DBConnection.closeConnection();
            }
        }));
    }
}
