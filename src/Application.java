import java.sql.*;

public class Application {

    private static Application instance;

    public static Application getInstance() {
        if (instance == null) {
            instance = new Application();
        }
        return instance;
    }

    private Connection connection;

    public Connection getDBConnection() {
        return connection;
    }

    private Application() {
        // Connect to MySQL database
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Update database name from hw4 to book_store
            String url = "jdbc:mysql://localhost:3306/book_store?useSSL=false&serverTimezone=UTC";
            String user = "root";       
            String password = "pass"; // TODO: Implement password through .env file 
            connection = DriverManager.getConnection(url, user, password);

            System.out.println("Successfully connected to the database.");

        } catch (ClassNotFoundException ex) {
            System.out.println("MySQL driver is not installed. System exits with error!");
            ex.printStackTrace();
            System.exit(1);
        } catch (SQLException ex) {
            System.out.println("MySQL database is not ready. System exits with error: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(2);
        }
    }

    public static void main(String[] args) {
        // Initialize the application and database connection
        Application.getInstance();
        
        // Start the login screen
        java.awt.EventQueue.invokeLater(() -> {
            new LoginScreen().setVisible(true);
        });
    }
}