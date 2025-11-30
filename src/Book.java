public class Book {
    private int bookId;
    private String title;
    private String author;
    private double priceBuy;
    private double priceRent;
    private boolean isAvailable;
    
    public Book() {}
    
    public Book(int bookId, String title, String author, double priceBuy, double priceRent, boolean isAvailable) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.priceBuy = priceBuy;
        this.priceRent = priceRent;
        this.isAvailable = isAvailable;
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
    
    public double getPriceBuy() {
        return priceBuy;
    }
    
    public double getPriceRent() {
        return priceRent;
    }
    
    public boolean isAvailable() {
        return isAvailable;
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
    
    public void setPriceBuy(double priceBuy) {
        this.priceBuy = priceBuy;
    }
    
    public void setPriceRent(double priceRent) {
        this.priceRent = priceRent;
    }
    
    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}