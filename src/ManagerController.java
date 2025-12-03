import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;

public class ManagerController {
    private final APIClient apiClient;
    private final Gson gson;
    
    public ManagerController() {
        this.apiClient = APIClient.getInstance();
        this.gson = new Gson();
    }
    
    /**
     * Get all orders via REST API
     */
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        
        try {
            String response = apiClient.get("/orders");
            JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
            JsonArray ordersArray = jsonResponse.getAsJsonArray("orders");
            
            for (JsonElement element : ordersArray) {
                JsonObject orderJson = element.getAsJsonObject();
                
                Order order = new Order();
                order.setOrderId(orderJson.get("order_id").getAsInt());
                order.setUserId(orderJson.get("user_id").getAsInt());
                order.setPaymentStatus(orderJson.get("payment_status").getAsString());
                order.setTotalAmount(orderJson.get("total_amount").getAsDouble());
                
                // Parse timestamp
                String createdAtStr = orderJson.get("created_at").getAsString();
                try {
                    // Convert ISO format to SQL format: 2024-01-15T14:30:22 -> 2024-01-15 14:30:22
                    createdAtStr = createdAtStr.replace("T", " ");
                    // Remove fractional seconds: 2024-01-15 14:30:22.123456 -> 2024-01-15 14:30:22
                    if (createdAtStr.contains(".")) {
                        createdAtStr = createdAtStr.substring(0, createdAtStr.indexOf("."));
                    }
                    // Remove timezone info: 2024-01-15 14:30:22+00:00 -> 2024-01-15 14:30:22
                    if (createdAtStr.contains("+")) {
                        createdAtStr = createdAtStr.substring(0, createdAtStr.indexOf("+"));
                    }
                    if (createdAtStr.contains("Z")) {
                        createdAtStr = createdAtStr.substring(0, createdAtStr.indexOf("Z"));
                    }
                    // Trim to exact format
                    createdAtStr = createdAtStr.trim();
                    if (createdAtStr.length() > 19) {
                        createdAtStr = createdAtStr.substring(0, 19);
                    }
                    order.setCreatedAt(Timestamp.valueOf(createdAtStr));
                } catch (Exception e) {
                    // If parsing fails, use current timestamp
                    System.err.println("Warning: Could not parse timestamp '" + createdAtStr + "', using current time");
                    order.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                }
                
                orders.add(order);
            }
            
        } catch (IOException e) {
            System.err.println("Get orders failed: " + e.getMessage());
        }
        
        return orders;
    }
    
    /**
     * Get username by ID (from orders response)
     */
    public String getUsernameById(int userId) {
        try {
            String response = apiClient.get("/orders");
            JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
            JsonArray ordersArray = jsonResponse.getAsJsonArray("orders");
            
            for (JsonElement element : ordersArray) {
                JsonObject orderJson = element.getAsJsonObject();
                if (orderJson.get("user_id").getAsInt() == userId) {
                    return orderJson.get("username").getAsString();
                }
            }
            
        } catch (IOException e) {
            System.err.println("Get username failed: " + e.getMessage());
        }
        
        return "Unknown";
    }
    
    /**
     * Update payment status via REST API
     */
    public boolean updatePaymentStatus(int orderId, String newStatus) {
        try {
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("payment_status", newStatus);
            
            apiClient.put("/orders/" + orderId + "/payment", gson.toJson(requestBody));
            return true;
            
        } catch (IOException e) {
            System.err.println("Update payment status failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get all books via REST API
     */
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        
        try {
            String response = apiClient.get("/books");
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
            System.err.println("Get books failed: " + e.getMessage());
        }
        
        return books;
    }
    
    /**
     * Add new book via REST API
     */
    public boolean addBook(Book book) {
        try {
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("title", book.getTitle());
            requestBody.addProperty("author", book.getAuthor());
            requestBody.addProperty("price_buy", book.getPriceBuy());
            requestBody.addProperty("price_rent", book.getPriceRent());
            requestBody.addProperty("is_available", book.isAvailable());
            
            apiClient.post("/books", gson.toJson(requestBody));
            return true;
            
        } catch (IOException e) {
            System.err.println("Add book failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update book via REST API
     */
    public boolean updateBook(Book book) {
        try {
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("title", book.getTitle());
            requestBody.addProperty("author", book.getAuthor());
            requestBody.addProperty("price_buy", book.getPriceBuy());
            requestBody.addProperty("price_rent", book.getPriceRent());
            requestBody.addProperty("is_available", book.isAvailable());
            
            apiClient.put("/books/" + book.getBookId(), gson.toJson(requestBody));
            return true;
            
        } catch (IOException e) {
            System.err.println("Update book failed: " + e.getMessage());
            return false;
        }
    }

    public List<OrderItem> getOrderItems(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        
        try {
            String response = apiClient.get("/orders/" + orderId + "/items");
            JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
            JsonArray itemsArray = jsonResponse.getAsJsonArray("items");
            
            for (JsonElement element : itemsArray) {
                JsonObject itemJson = element.getAsJsonObject();
                
                OrderItem item = new OrderItem();
                item.setOrderItemId(itemJson.get("order_item_id").getAsInt());
                item.setBookId(itemJson.get("book_id").getAsInt());
                item.setBookTitle(itemJson.get("title").getAsString());
                item.setBookAuthor(itemJson.get("author").getAsString());
                item.setItemType(itemJson.get("item_type").getAsString());
                item.setPrice(itemJson.get("price").getAsDouble());
                
                items.add(item);
            }
            
        } catch (IOException e) {
            System.err.println("Get order items failed: " + e.getMessage());
        }
        
        return items;
    }
}