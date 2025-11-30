import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerScreen extends JFrame {
    private JTextField searchField;
    private JButton searchButton;
    private JButton logoutButton;
    private JTable booksTable;
    private DefaultTableModel tableModel;
    private JButton addToCartButton;
    private JButton viewCartButton;
    private JComboBox<String> actionTypeCombo;
    private CustomerController controller;
    private List<CartItem> cart;

    public CustomerScreen() {
        controller = new CustomerController();
        cart = new ArrayList<>();
        initializeUI();
    }

    private void initializeUI() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        setTitle("Online Bookstore - Customer Dashboard (" + currentUser.getUsername() + ")");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel with search and logout
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout(10, 10));

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search Books:"));
        searchField = new JTextField(30);
        searchPanel.add(searchField);
        searchButton = new JButton("Search");
        searchPanel.add(searchButton);

        logoutButton = new JButton("Logout");
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.add(logoutButton);

        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(logoutPanel, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center panel with table
        String[] columnNames = {"Book ID", "Title", "Author", "Buy Price", "Rent Price", "Available"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        booksTable = new JTable(tableModel);
        booksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(booksTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with cart actions
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        bottomPanel.add(new JLabel("Action:"));
        actionTypeCombo = new JComboBox<>(new String[]{"Buy", "Rent"});
        bottomPanel.add(actionTypeCombo);
        
        addToCartButton = new JButton("Add to Cart");
        bottomPanel.add(addToCartButton);
        
        viewCartButton = new JButton("View Cart (" + cart.size() + ")");
        bottomPanel.add(viewCartButton);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Action listeners
        searchButton.addActionListener(e -> performSearch());
        searchField.addActionListener(e -> performSearch());
        addToCartButton.addActionListener(e -> addToCart());
        viewCartButton.addActionListener(e -> openCart());
        logoutButton.addActionListener(e -> logout());

        // Load all books initially
        performSearch();
    }

    private void performSearch() {
        String keyword = searchField.getText().trim();
        List<Book> books = controller.searchBooks(keyword);
        
        // Clear existing rows
        tableModel.setRowCount(0);
        
        // Add search results
        for (Book book : books) {
            Object[] row = {
                book.getBookId(),
                book.getTitle(),
                book.getAuthor(),
                String.format("$%.2f", book.getPriceBuy()),
                String.format("$%.2f", book.getPriceRent()),
                book.isAvailable() ? "Yes" : "No"
            };
            tableModel.addRow(row);
        }
    }

    private void addToCart() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a book to add to cart.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookId = (int) tableModel.getValueAt(selectedRow, 0);
        String title = (String) tableModel.getValueAt(selectedRow, 1);
        String author = (String) tableModel.getValueAt(selectedRow, 2);
        String actionType = actionTypeCombo.getSelectedItem().toString().toLowerCase();
        
        // Get the price based on action type
        String priceStr = (String) tableModel.getValueAt(selectedRow, actionType.equals("buy") ? 3 : 4);
        double price = Double.parseDouble(priceStr.substring(1)); // Remove $

        CartItem item = new CartItem(bookId, title, author, actionType, price);
        cart.add(item);
        
        viewCartButton.setText("View Cart (" + cart.size() + ")");
        
        JOptionPane.showMessageDialog(this,
            "Added '" + title + "' to cart (" + actionType + ").",
            "Success",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void openCart() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Your cart is empty.",
                "Empty Cart",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        new CartScreen(cart, this).setVisible(true);
    }

    public void clearCart() {
        cart.clear();
        viewCartButton.setText("View Cart (" + cart.size() + ")");
    }

    private void logout() {
        SessionManager.getInstance().logout();
        this.setVisible(false);
        new LoginScreen().setVisible(true);
        this.dispose();
    }
}