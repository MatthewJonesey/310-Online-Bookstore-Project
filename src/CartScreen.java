import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CartScreen extends JFrame {
    private JTable cartTable;
    private DefaultTableModel tableModel;
    private JLabel totalLabel;
    private JButton placeOrderButton;
    private JButton cancelButton;
    private List<CartItem> cart;
    private CustomerScreen parentScreen;
    private CustomerController controller;

    public CartScreen(List<CartItem> cart, CustomerScreen parentScreen) {
        this.cart = cart;
        this.parentScreen = parentScreen;
        this.controller = new CustomerController();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Shopping Cart");
        setSize(700, 400);
        setLocationRelativeTo(parentScreen);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Your Shopping Cart", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"Book Title", "Author", "Action", "Price"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (CartItem item : cart) {
            Object[] row = {
                item.getTitle(),
                item.getAuthor(),
                item.getActionType().substring(0, 1).toUpperCase() + item.getActionType().substring(1),
                String.format("$%.2f", item.getPrice())
            };
            tableModel.addRow(row);
        }

        cartTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(cartTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout(10, 10));

        // Total
        double total = cart.stream().mapToDouble(CartItem::getPrice).sum();
        totalLabel = new JLabel(String.format("Total Amount: $%.2f", total), SwingConstants.RIGHT);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        bottomPanel.add(totalLabel, BorderLayout.NORTH);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        placeOrderButton = new JButton("Place Order");
        cancelButton = new JButton("Cancel");

        buttonPanel.add(placeOrderButton);
        buttonPanel.add(cancelButton);

        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Action listeners
        placeOrderButton.addActionListener(e -> placeOrder());
        cancelButton.addActionListener(e -> dispose());
    }

    private void placeOrder() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to place this order?",
            "Confirm Order",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int orderId = controller.createOrder(cart);
            
            if (orderId > 0) {
                // Email is sent automatically by Flask backend
                JOptionPane.showMessageDialog(this,
                    "Order placed successfully!\nOrder ID: " + orderId + 
                    "\nA confirmation email has been sent to your registered email.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                
                parentScreen.clearCart();
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to place order. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}