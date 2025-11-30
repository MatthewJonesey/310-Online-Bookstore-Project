import java.sql.Timestamp;
import java.util.List;

// Order.java
public class Order {
    private int orderId;
    private int userId;
    private String paymentStatus;
    private double totalAmount;
    private Timestamp createdAt;
    private String userEmail;
    private List<OrderItem> items;
    
    public Order() {}
    
    // Getters
    public int getOrderId() {
        return orderId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public String getPaymentStatus() {
        return paymentStatus;
    }
    
    public double getTotalAmount() {
        return totalAmount;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public String getUserEmail() {
        return userEmail;
    }
    
    public List<OrderItem> getItems() {
        return items;
    }
    
    // Setters
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    
    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}