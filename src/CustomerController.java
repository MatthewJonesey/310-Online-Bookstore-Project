import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomerController {
    private final APIClient apiClient;
    private final Gson gson;
    
    public CustomerController() {
        this.apiClient = APIClient.getInstance();
        this.gson = new Gson();
    }
    
    /**
     * Search books via REST API
     */
    public List<Book> searchBooks(String keyword) {
        List<Book> books = new ArrayList<>();
        
        try {
            String endpoint = "/books/search";
            if (keyword != null && !keyword.isEmpty()) {
                endpoint += "?keyword=" + java.net.URLEncoder.encode(keyword, "UTF-8");
            }
            
            String response = apiClient.get(endpoint);
            JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
            JsonArray booksArray = jsonResponse.getAsJsonArray("books");
            
            for (JsonElement element : booksArray) {
                JsonObject bookJson = element.getAsJsonObject();
                
                Book book = new Book();
                book.setBookId(bookJson.get("book_id").getAsInt());
                book.setTitle(bookJson.get("title").getAsString());
                book.setAuthor(bookJson.get("author").getAsString());
                book.setPriceBuy(bookJson.get("price_buy").getAsDouble());
                book.setPriceRent(bookJson.get("price_rent").getAsDouble());
                book.setAvailable(bookJson.get("is_available").getAsInt() == 1);
                
                books.add(book);
            }
            
        } catch (IOException e) {
            System.err.println("Search books failed: " + e.getMessage());
        }
        
        return books;
    }
    
    /**
     * Create order via REST API
     */
    public int createOrder(List<CartItem> cartItems) {
        try {
            // Build request JSON
            JsonObject requestBody = new JsonObject();
            JsonArray itemsArray = new JsonArray();
            
            for (CartItem item : cartItems) {
                JsonObject itemJson = new JsonObject();
                itemJson.addProperty("book_id", item.getBookId());
                itemJson.addProperty("item_type", item.getActionType());
                itemJson.addProperty("price", item.getPrice());
                itemsArray.add(itemJson);
            }
            
            requestBody.add("items", itemsArray);
            
            // Make API call
            String response = apiClient.post("/orders", gson.toJson(requestBody));
            JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
            
            return jsonResponse.get("order_id").getAsInt();
            
        } catch (IOException e) {
            System.err.println("Create order failed: " + e.getMessage());
            return -1;
        }
    }
    
    /**
     * Get order details via REST API
     */
    public Order getOrderDetails(int orderId) {
        try {
            String response = apiClient.get("/orders");
            JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
            JsonArray ordersArray = jsonResponse.getAsJsonArray("orders");
            
            // Find the specific order
            for (JsonElement element : ordersArray) {
                JsonObject orderJson = element.getAsJsonObject();
                if (orderJson.get("order_id").getAsInt() == orderId) {
                    Order order = new Order();
                    order.setOrderId(orderJson.get("order_id").getAsInt());
                    order.setUserId(orderJson.get("user_id").getAsInt());
                    order.setPaymentStatus(orderJson.get("payment_status").getAsString());
                    order.setTotalAmount(orderJson.get("total_amount").getAsDouble());
                    
                    // For email, we need the user's email from session
                    User currentUser = SessionManager.getInstance().getCurrentUser();
                    if (currentUser != null) {
                        order.setUserEmail(currentUser.getEmail());
                    }
                    
                    return order;
                }
            }
            
        } catch (IOException e) {
            System.err.println("Get order details failed: " + e.getMessage());
        }
        
        return null;
    }
}