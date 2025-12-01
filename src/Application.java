public class Application {
    private static Application instance;

    public static Application getInstance() {
        if (instance == null) {
            instance = new Application();
        }
        return instance;
    }

    private Application() {
        // No database connection - Flask API handles all database operations
        System.out.println("Application initialized - using REST API backend at http://localhost:5000");
    }

    public static void main(String[] args) {
        Application.getInstance();
        
        // Start the login screen
        java.awt.EventQueue.invokeLater(() -> {
            new LoginScreen().setVisible(true);
        });
    }
}