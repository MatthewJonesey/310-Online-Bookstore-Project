class OrderItem {
    private int orderItemId;
    private int bookId;
    private String bookTitle;
    private String bookAuthor;
    private String itemType;
    private double price;
    
    public OrderItem() {}
    
    // Getters
    public int getOrderItemId() {
        return orderItemId;
    }
    
    public int getBookId() {
        return bookId;
    }
    
    public String getBookTitle() {
        return bookTitle;
    }
    
    public String getBookAuthor() {
        return bookAuthor;
    }
    
    public String getItemType() {
        return itemType;
    }
    
    public double getPrice() {
        return price;
    }
    
    // Setters
    public void setOrderItemId(int orderItemId) {
        this.orderItemId = orderItemId;
    }
    
    public void setBookId(int bookId) {
        this.bookId = bookId;
    }
    
    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }
    
    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }
    
    public void setItemType(String itemType) {
        this.itemType = itemType;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
}