public class CartItem {
    private int bookId;
    private String title;
    private String author;
    private String actionType; // "buy" or "rent"
    private double price;
    
    public CartItem() {}
    
    public CartItem(int bookId, String title, String author, String actionType, double price) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.actionType = actionType;
        this.price = price;
    }
    
    // Getters
    public int getBookId() {
        return bookId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public String getActionType() {
        return actionType;
    }
    
    public double getPrice() {
        return price;
    }
    
    // Setters
    public void setBookId(int bookId) {
        this.bookId = bookId;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public void setActionType(String actionType) {
        this.actionType = actionType;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
}