import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;

public class LoginController {
    private final APIClient apiClient;
    private final Gson gson;
    
    public LoginController() {
        this.apiClient = APIClient.getInstance();
        this.gson = new Gson();
    }
    
    /**
     * Login user via REST API
     */
    public User login(String username, String password) {
        try {
            // Create request JSON
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("username", username);
            requestBody.addProperty("password", password);
            
            // Make API call
            String response = apiClient.post("/auth/login", gson.toJson(requestBody));
            
            // Parse response
            JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
            
            // Store auth token
            String token = jsonResponse.get("token").getAsString();
            apiClient.setAuthToken(token);
            
            // Extract user data
            JsonObject userJson = jsonResponse.getAsJsonObject("user");
            
            User user = new User();
            user.setUserId(userJson.get("user_id").getAsInt());
            user.setUsername(userJson.get("username").getAsString());
            user.setEmail(userJson.get("email").getAsString());
            user.setRole(userJson.get("role").getAsString());
            
            return user;
            
        } catch (IOException e) {
            System.err.println("Login failed: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Register new user via REST API
     */
    public boolean register(String username, String email, String password) {
        try {
            // Create request JSON
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("username", username);
            requestBody.addProperty("email", email);
            requestBody.addProperty("password", password);
            
            // Make API call
            apiClient.post("/auth/register", gson.toJson(requestBody));
            
            return true;
            
        } catch (IOException e) {
            System.err.println("Registration failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Logout user
     */
    public void logout() {
        try {
            apiClient.post("/auth/logout", null);
        } catch (IOException e) {
            System.err.println("Logout error: " + e.getMessage());
        } finally {
            apiClient.clearAuthToken();
        }
    }
}