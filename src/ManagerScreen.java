import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManagerScreen extends JFrame {
    private JTabbedPane tabbedPane;
    private JButton logoutButton;
    private ManagerController controller;
    
    // Orders tab components
    private JTable ordersTable;
    private DefaultTableModel ordersTableModel;
    private JButton refreshOrdersButton;
    private JButton viewOrderDetailsButton;
    private JButton updatePaymentButton;
    
    // Books tab components
    private JTable booksTable;
    private DefaultTableModel booksTableModel;
    private JButton addBookButton;
    private JButton updateBookButton;
    private JButton refreshBooksButton;

    public ManagerScreen() {
        controller = new ManagerController();
        initializeUI();
        loadOrders();
        loadBooks();
    }

    private void initializeUI() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        setTitle("Online Bookstore - Manager Dashboard (" + currentUser.getUsername() + ")");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel with logout
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        
        JLabel welcomeLabel = new JLabel("Manager Dashboard");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(welcomeLabel, BorderLayout.WEST);
        
        logoutButton = new JButton("Logout");
        topPanel.add(logoutButton, BorderLayout.EAST);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Orders tab
        JPanel ordersPanel = createOrdersPanel();
        tabbedPane.addTab("Orders", ordersPanel);
        
        // Books tab
        JPanel booksPanel = createBooksPanel();
        tabbedPane.addTab("Books", booksPanel);
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);

        // Action listeners
        logoutButton.addActionListener(e -> logout());
        refreshOrdersButton.addActionListener(e -> loadOrders());
        viewOrderDetailsButton.addActionListener(e -> viewOrderDetails());
        updatePaymentButton.addActionListener(e -> updatePaymentStatus());
        refreshBooksButton.addActionListener(e -> loadBooks());
        addBookButton.addActionListener(e -> openAddBookDialog());
        updateBookButton.addActionListener(e -> openUpdateBookDialog());
    }

    private JPanel createOrdersPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table
        String[] columnNames = {"Order ID", "User ID", "Username", "Total Amount", "Payment Status", "Order Date"};
        ordersTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ordersTable = new JTable(ordersTableModel);
        ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        ordersTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    viewOrderDetails();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(ordersTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        refreshOrdersButton = new JButton("Refresh Orders");
        viewOrderDetailsButton = new JButton("View Order Details");
        updatePaymentButton = new JButton("Update Payment Status");
        
        buttonPanel.add(refreshOrdersButton);
        buttonPanel.add(viewOrderDetailsButton);
        buttonPanel.add(updatePaymentButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createBooksPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table
        String[] columnNames = {"Book ID", "Title", "Author", "Buy Price", "Rent Price", "Available"};
        booksTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        booksTable = new JTable(booksTableModel);
        booksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(booksTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        refreshBooksButton = new JButton("Refresh Books");
        addBookButton = new JButton("Add Book");
        updateBookButton = new JButton("Update Book");
        
        buttonPanel.add(refreshBooksButton);
        buttonPanel.add(addBookButton);
        buttonPanel.add(updateBookButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadOrders() {
        List<Order> orders = controller.getAllOrders();
        ordersTableModel.setRowCount(0);
        
        for (Order order : orders) {
            Object[] row = {
                order.getOrderId(),
                order.getUserId(),
                controller.getUsernameById(order.getUserId()),
                String.format("$%.2f", order.getTotalAmount()),
                order.getPaymentStatus(),
                order.getCreatedAt()
            };
            ordersTableModel.addRow(row);
        }
    }

    private void viewOrderDetails() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select an order to view details.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int orderId = (int) ordersTableModel.getValueAt(selectedRow, 0);
        int userId = (int) ordersTableModel.getValueAt(selectedRow, 1);
        String username = (String) ordersTableModel.getValueAt(selectedRow, 2);
        String totalAmount = (String) ordersTableModel.getValueAt(selectedRow, 3);
        String paymentStatus = (String) ordersTableModel.getValueAt(selectedRow, 4);
        Object orderDate = ordersTableModel.getValueAt(selectedRow, 5);

        // Get order items
        List<OrderItem> items = controller.getOrderItems(orderId);

        // Create dialog
        JDialog detailsDialog = new JDialog(this, "Order Details - Order #" + orderId, true);
        detailsDialog.setSize(700, 500);
        detailsDialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Order summary panel
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new GridLayout(5, 2, 10, 5));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Order Summary"));

        summaryPanel.add(new JLabel("Order ID:"));
        summaryPanel.add(new JLabel(String.valueOf(orderId)));
        summaryPanel.add(new JLabel("Customer:"));
        summaryPanel.add(new JLabel(username + " (ID: " + userId + ")"));
        summaryPanel.add(new JLabel("Total Amount:"));
        summaryPanel.add(new JLabel(totalAmount));
        summaryPanel.add(new JLabel("Payment Status:"));
        summaryPanel.add(new JLabel(paymentStatus));
        summaryPanel.add(new JLabel("Order Date:"));
        summaryPanel.add(new JLabel(orderDate.toString()));

        mainPanel.add(summaryPanel, BorderLayout.NORTH);

        // Items table
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BorderLayout());
        itemsPanel.setBorder(BorderFactory.createTitledBorder("Order Items"));

        String[] columnNames = {"Item ID", "Book Title", "Author", "Type", "Price"};
        DefaultTableModel itemsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (OrderItem item : items) {
            Object[] row = {
                item.getOrderItemId(),
                item.getBookTitle(),
                item.getBookAuthor(),
                item.getItemType().substring(0, 1).toUpperCase() + item.getItemType().substring(1),
                String.format("$%.2f", item.getPrice())
            };
            itemsTableModel.addRow(row);
        }

        JTable itemsTable = new JTable(itemsTableModel);
        itemsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(itemsTable);
        itemsPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(itemsPanel, BorderLayout.CENTER);

        // Close button
        JPanel buttonPanel = new JPanel();
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> detailsDialog.dispose());
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        detailsDialog.add(mainPanel);
        detailsDialog.setVisible(true);
    }

    private void updatePaymentStatus() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select an order to update.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int orderId = (int) ordersTableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) ordersTableModel.getValueAt(selectedRow, 4);

        String[] options = {"Pending", "Paid"};
        String newStatus = (String) JOptionPane.showInputDialog(this,
            "Select new payment status:",
            "Update Payment Status",
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            currentStatus);

        if (newStatus != null && !newStatus.equals(currentStatus)) {
            boolean success = controller.updatePaymentStatus(orderId, newStatus);
            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Payment status updated successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                loadOrders();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to update payment status.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadBooks() {
        List<Book> books = controller.getAllBooks();
        booksTableModel.setRowCount(0);
        
        for (Book book : books) {
            Object[] row = {
                book.getBookId(),
                book.getTitle(),
                book.getAuthor(),
                String.format("$%.2f", book.getPriceBuy()),
                String.format("$%.2f", book.getPriceRent()),
                book.isAvailable() ? "Yes" : "No"
            };
            booksTableModel.addRow(row);
        }
    }

    private void openAddBookDialog() {
        JTextField titleField = new JTextField(20);
        JTextField authorField = new JTextField(20);
        JTextField buyPriceField = new JTextField(10);
        JTextField rentPriceField = new JTextField(10);
        JCheckBox availableCheckBox = new JCheckBox("Available");
        availableCheckBox.setSelected(true);

        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Author:"));
        panel.add(authorField);
        panel.add(new JLabel("Buy Price:"));
        panel.add(buyPriceField);
        panel.add(new JLabel("Rent Price:"));
        panel.add(rentPriceField);
        panel.add(new JLabel("Availability:"));
        panel.add(availableCheckBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Book",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String title = titleField.getText().trim();
                String author = authorField.getText().trim();
                double buyPrice = Double.parseDouble(buyPriceField.getText().trim());
                double rentPrice = Double.parseDouble(rentPriceField.getText().trim());
                boolean available = availableCheckBox.isSelected();

                if (title.isEmpty() || author.isEmpty()) {
                    throw new IllegalArgumentException("Title and author cannot be empty.");
                }

                Book book = new Book();
                book.setTitle(title);
                book.setAuthor(author);
                book.setPriceBuy(buyPrice);
                book.setPriceRent(rentPrice);
                book.setAvailable(available);

                boolean success = controller.addBook(book);
                if (success) {
                    JOptionPane.showMessageDialog(this,
                        "Book added successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    loadBooks();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to add book.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Invalid input: " + e.getMessage(),
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openUpdateBookDialog() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a book to update.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookId = (int) booksTableModel.getValueAt(selectedRow, 0);
        String currentTitle = (String) booksTableModel.getValueAt(selectedRow, 1);
        String currentAuthor = (String) booksTableModel.getValueAt(selectedRow, 2);
        String currentBuyPrice = ((String) booksTableModel.getValueAt(selectedRow, 3)).substring(1);
        String currentRentPrice = ((String) booksTableModel.getValueAt(selectedRow, 4)).substring(1);
        boolean currentAvailable = booksTableModel.getValueAt(selectedRow, 5).equals("Yes");

        JTextField titleField = new JTextField(currentTitle, 20);
        JTextField authorField = new JTextField(currentAuthor, 20);
        JTextField buyPriceField = new JTextField(currentBuyPrice, 10);
        JTextField rentPriceField = new JTextField(currentRentPrice, 10);
        JCheckBox availableCheckBox = new JCheckBox("Available");
        availableCheckBox.setSelected(currentAvailable);

        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Author:"));
        panel.add(authorField);
        panel.add(new JLabel("Buy Price:"));
        panel.add(buyPriceField);
        panel.add(new JLabel("Rent Price:"));
        panel.add(rentPriceField);
        panel.add(new JLabel("Availability:"));
        panel.add(availableCheckBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Update Book",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Book book = new Book();
                book.setBookId(bookId);
                book.setTitle(titleField.getText().trim());
                book.setAuthor(authorField.getText().trim());
                book.setPriceBuy(Double.parseDouble(buyPriceField.getText().trim()));
                book.setPriceRent(Double.parseDouble(rentPriceField.getText().trim()));
                book.setAvailable(availableCheckBox.isSelected());

                boolean success = controller.updateBook(book);
                if (success) {
                    JOptionPane.showMessageDialog(this,
                        "Book updated successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    loadBooks();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to update book.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Invalid input: " + e.getMessage(),
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void logout() {
        SessionManager.getInstance().logout();
        this.setVisible(false);
        new LoginScreen().setVisible(true);
        this.dispose();
    }
}