import java.sql.*;

public class Application {

    private static Application instance;   // Singleton pattern

    public static Application getInstance() {
        if (instance == null) {
            instance = new Application();
        }
        return instance;
    }
    // Main components of this application

    private Connection connection;

    public Connection getDBConnection() {
        return connection;
    }


    private Application() {
        // Connect to my sql data base changed from slqlite database
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = "jdbc:mysql://localhost:3306/hw4?useSSL=false&serverTimezone=UTC";
            String user = "root";       
            String password = "Jones5960495"; 
            connection = DriverManager.getConnection(url,user,password);

        }
        catch (ClassNotFoundException ex) {
            System.out.println("MySQL is not installed. System exits with error!");
            ex.printStackTrace();
            System.exit(1);
        }

        catch (SQLException ex) {
            System.out.println("MySQL database is not ready. System exits with error!" + ex.getMessage());

            System.exit(2);
        }

    }


    public static void main(String[] args) {
        // TODO Implement Login screen
        //Application.getInstance().getLoginScreen().setVisible(true);
    }
}
