import com.google.gson.Gson;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * HTTP client for making REST API calls to the Flask backend
 */
public class APIClient {
    private static final String BASE_URL = "http://localhost:5000/api";
    private static final Gson gson = new Gson();
    private String authToken;
    
    private static APIClient instance;
    
    public static APIClient getInstance() {
        if (instance == null) {
            instance = new APIClient();
        }
        return instance;
    }
    
    private APIClient() {}
    
    public void setAuthToken(String token) {
        this.authToken = token;
    }
    
    public String getAuthToken() {
        return authToken;
    }
    
    public void clearAuthToken() {
        this.authToken = null;
    }
    
    /**
     * Make HTTP request to API
     */
    private String makeRequest(String endpoint, String method, String jsonBody) throws IOException {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json");
        
        // Add auth token if present
        if (authToken != null) {
            conn.setRequestProperty("Authorization", authToken);
        }
        
        // Send request body if provided
        if (jsonBody != null) {
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
        }
        
        // Read response
        int responseCode = conn.getResponseCode();
        InputStream inputStream = responseCode < 400 ? conn.getInputStream() : conn.getErrorStream();
        
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            
            if (responseCode >= 400) {
                throw new IOException("HTTP " + responseCode + ": " + response.toString());
            }
            
            return response.toString();
        }
    }
    
    /**
     * Make GET request
     */
    public String get(String endpoint) throws IOException {
        return makeRequest(endpoint, "GET", null);
    }
    
    /**
     * Make POST request
     */
    public String post(String endpoint, String jsonBody) throws IOException {
        return makeRequest(endpoint, "POST", jsonBody);
    }
    
    /**
     * Make PUT request
     */
    public String put(String endpoint, String jsonBody) throws IOException {
        return makeRequest(endpoint, "PUT", jsonBody);
    }
    
    /**
     * Make DELETE request
     */
    public String delete(String endpoint) throws IOException {
        return makeRequest(endpoint, "DELETE", null);
    }
}