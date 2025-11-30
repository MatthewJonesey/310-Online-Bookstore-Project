import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class LoginController {
    
    public User login(String username, String password) {
        Connection conn = Application.getInstance().getDBConnection();
        String query = "SELECT user_id, username, email, password_hash, role FROM users WHERE username = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                
                // Verify password using BCrypt
                if (BCrypt.checkpw(password, storedHash)) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(rs.getString("role"));
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public boolean register(String username, String email, String password) {
        Connection conn = Application.getInstance().getDBConnection();
        String query = "INSERT INTO users (username, email, password_hash, role) VALUES (?, ?, ?, 'customer')";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            // Hash the password using BCrypt
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
            
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, hashedPassword);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}