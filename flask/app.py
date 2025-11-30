from flask import Flask, jsonify, request
from flask_cors import CORS
import mysql.connector
from mysql.connector import Error
import bcrypt
import smtplib
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart
from datetime import datetime
import os

app = Flask(__name__)
CORS(app)  # Enable CORS for desktop client

# Database configuration
DB_CONFIG = {
    'host': 'localhost',
    'database': 'book_store',
    'user': 'root',
    'password': 'pass'
}

# Email configuration
SMTP_HOST = 'smtp.gmail.com'
SMTP_PORT = 587
SENDER_EMAIL = 'your-email@gmail.com'
SENDER_PASSWORD = 'your-app-password'

# Active sessions (in production, use Redis or similar)
sessions = {}

def get_db_connection():
    """Create and return database connection"""
    try:
        connection = mysql.connector.connect(**DB_CONFIG)
        return connection
    except Error as e:
        print(f"Database connection error: {e}")
        return None

def send_email(to_email, subject, body):
    """Send email notification"""
    try:
        msg = MIMEMultipart()
        msg['From'] = SENDER_EMAIL
        msg['To'] = to_email
        msg['Subject'] = subject
        msg.attach(MIMEText(body, 'plain'))
        
        server = smtplib.SMTP(SMTP_HOST, SMTP_PORT)
        server.starttls()
        server.login(SENDER_EMAIL, SENDER_PASSWORD)
        server.send_message(msg)
        server.quit()
        return True
    except Exception as e:
        print(f"Email error: {e}")
        return False

# ============================================================================
# AUTHENTICATION ENDPOINTS
# ============================================================================

@app.route('/api/auth/register', methods=['POST'])
def register():
    """Register a new user"""
    data = request.get_json()
    username = data.get('username')
    email = data.get('email')
    password = data.get('password')
    
    if not all([username, email, password]):
        return jsonify({'error': 'All fields required'}), 400
    
    # Hash password
    password_hash = bcrypt.hashpw(password.encode('utf-8'), bcrypt.gensalt(12))
    
    conn = get_db_connection()
    if not conn:
        return jsonify({'error': 'Database connection failed'}), 500
    
    try:
        cursor = conn.cursor()
        query = "INSERT INTO users (username, email, password_hash, role) VALUES (%s, %s, %s, 'customer')"
        cursor.execute(query, (username, email, password_hash))
        conn.commit()
        
        return jsonify({
            'message': 'Registration successful',
            'user_id': cursor.lastrowid
        }), 201
        
    except Error as e:
        return jsonify({'error': f'Registration failed: {str(e)}'}), 400
    finally:
        cursor.close()
        conn.close()

@app.route('/api/auth/login', methods=['POST'])
def login():
    """Authenticate user and create session"""
    data = request.get_json()
    username = data.get('username')
    password = data.get('password')
    
    if not all([username, password]):
        return jsonify({'error': 'Username and password required'}), 400
    
    conn = get_db_connection()
    if not conn:
        return jsonify({'error': 'Database connection failed'}), 500
    
    try:
        cursor = conn.cursor(dictionary=True)
        query = "SELECT user_id, username, email, password_hash, role FROM users WHERE username = %s"
        cursor.execute(query, (username,))
        user = cursor.fetchone()
        
        if user and bcrypt.checkpw(password.encode('utf-8'), user['password_hash'].encode('utf-8')):
            # Create session token (in production, use JWT)
            session_token = f"{user['user_id']}_{datetime.now().timestamp()}"
            sessions[session_token] = {
                'user_id': user['user_id'],
                'username': user['username'],
                'email': user['email'],
                'role': user['role']
            }
            
            return jsonify({
                'message': 'Login successful',
                'token': session_token,
                'user': {
                    'user_id': user['user_id'],
                    'username': user['username'],
                    'email': user['email'],
                    'role': user['role']
                }
            }), 200
        else:
            return jsonify({'error': 'Invalid credentials'}), 401
            
    finally:
        cursor.close()
        conn.close()

@app.route('/api/auth/logout', methods=['POST'])
def logout():
    """Logout user and destroy session"""
    token = request.headers.get('Authorization')
    if token and token in sessions:
        del sessions[token]
    return jsonify({'message': 'Logout successful'}), 200

# ============================================================================
# BOOK ENDPOINTS
# ============================================================================

@app.route('/api/books/search', methods=['GET'])
def search_books():
    """Search books by keyword"""
    token = request.headers.get('Authorization')
    if not token or token not in sessions:
        return jsonify({'error': 'Unauthorized'}), 401
    
    keyword = request.args.get('keyword', '')
    
    conn = get_db_connection()
    if not conn:
        return jsonify({'error': 'Database connection failed'}), 500
    
    try:
        cursor = conn.cursor(dictionary=True)
        
        if keyword:
            query = """SELECT book_id, title, author, price_buy, price_rent, is_available 
                      FROM books WHERE is_available = TRUE AND (title LIKE %s OR author LIKE %s)"""
            cursor.execute(query, (f'%{keyword}%', f'%{keyword}%'))
        else:
            query = "SELECT book_id, title, author, price_buy, price_rent, is_available FROM books WHERE is_available = TRUE"
            cursor.execute(query)
        
        books = cursor.fetchall()
        return jsonify({'books': books}), 200
        
    finally:
        cursor.close()
        conn.close()

@app.route('/api/books', methods=['GET'])
def get_all_books():
    """Get all books (manager only)"""
    token = request.headers.get('Authorization')
    if not token or token not in sessions:
        return jsonify({'error': 'Unauthorized'}), 401
    
    if sessions[token]['role'] != 'manager':
        return jsonify({'error': 'Forbidden'}), 403
    
    conn = get_db_connection()
    if not conn:
        return jsonify({'error': 'Database connection failed'}), 500
    
    try:
        cursor = conn.cursor(dictionary=True)
        query = "SELECT * FROM books ORDER BY title"
        cursor.execute(query)
        books = cursor.fetchall()
        return jsonify({'books': books}), 200
        
    finally:
        cursor.close()
        conn.close()

@app.route('/api/books', methods=['POST'])
def add_book():
    """Add new book (manager only)"""
    token = request.headers.get('Authorization')
    if not token or token not in sessions:
        return jsonify({'error': 'Unauthorized'}), 401
    
    if sessions[token]['role'] != 'manager':
        return jsonify({'error': 'Forbidden'}), 403
    
    data = request.get_json()
    title = data.get('title')
    author = data.get('author')
    price_buy = data.get('price_buy')
    price_rent = data.get('price_rent')
    is_available = data.get('is_available', True)
    
    if not all([title, author, price_buy, price_rent]):
        return jsonify({'error': 'All fields required'}), 400
    
    conn = get_db_connection()
    if not conn:
        return jsonify({'error': 'Database connection failed'}), 500
    
    try:
        cursor = conn.cursor()
        query = """INSERT INTO books (title, author, price_buy, price_rent, is_available) 
                  VALUES (%s, %s, %s, %s, %s)"""
        cursor.execute(query, (title, author, price_buy, price_rent, is_available))
        conn.commit()
        
        return jsonify({
            'message': 'Book added successfully',
            'book_id': cursor.lastrowid
        }), 201
        
    finally:
        cursor.close()
        conn.close()

@app.route('/api/books/<int:book_id>', methods=['PUT'])
def update_book(book_id):
    """Update book (manager only)"""
    token = request.headers.get('Authorization')
    if not token or token not in sessions:
        return jsonify({'error': 'Unauthorized'}), 401
    
    if sessions[token]['role'] != 'manager':
        return jsonify({'error': 'Forbidden'}), 403
    
    data = request.get_json()
    
    conn = get_db_connection()
    if not conn:
        return jsonify({'error': 'Database connection failed'}), 500
    
    try:
        cursor = conn.cursor()
        query = """UPDATE books SET title = %s, author = %s, price_buy = %s, 
                  price_rent = %s, is_available = %s WHERE book_id = %s"""
        cursor.execute(query, (
            data.get('title'),
            data.get('author'),
            data.get('price_buy'),
            data.get('price_rent'),
            data.get('is_available'),
            book_id
        ))
        conn.commit()
        
        if cursor.rowcount > 0:
            return jsonify({'message': 'Book updated successfully'}), 200
        else:
            return jsonify({'error': 'Book not found'}), 404
        
    finally:
        cursor.close()
        conn.close()

# ============================================================================
# ORDER ENDPOINTS
# ============================================================================

@app.route('/api/orders', methods=['POST'])
def create_order():
    """Create new order"""
    token = request.headers.get('Authorization')
    if not token or token not in sessions:
        return jsonify({'error': 'Unauthorized'}), 401
    
    user = sessions[token]
    data = request.get_json()
    cart_items = data.get('items', [])
    
    if not cart_items:
        return jsonify({'error': 'Cart is empty'}), 400
    
    conn = get_db_connection()
    if not conn:
        return jsonify({'error': 'Database connection failed'}), 500
    
    try:
        cursor = conn.cursor(dictionary=True)
        conn.start_transaction()
        
        # Calculate total
        total_amount = sum(item['price'] for item in cart_items)
        
        # Create order
        order_query = "INSERT INTO orders (user_id, payment_status, total_amount) VALUES (%s, 'Pending', %s)"
        cursor.execute(order_query, (user['user_id'], total_amount))
        order_id = cursor.lastrowid
        
        # Add order items
        item_query = "INSERT INTO order_items (order_id, book_id, item_type, price) VALUES (%s, %s, %s, %s)"
        for item in cart_items:
            cursor.execute(item_query, (
                order_id,
                item['book_id'],
                item['item_type'],
                item['price']
            ))
        
        conn.commit()
        
        # Get order details for email
        cursor.execute("""
            SELECT o.order_id, o.total_amount, o.created_at,
                   oi.order_item_id, b.title, b.author, oi.item_type, oi.price
            FROM orders o
            JOIN order_items oi ON o.order_id = oi.order_id
            JOIN books b ON oi.book_id = b.book_id
            WHERE o.order_id = %s
        """, (order_id,))
        
        order_details = cursor.fetchall()
        
        # Send email
        email_body = f"""Dear {user['username']},

Thank you for your order!

Order Details:
Order ID: {order_id}
Order Date: {order_details[0]['created_at']}
Payment Status: Pending

Items:
---------------------------------------------
"""
        for detail in order_details:
            email_body += f"{detail['title']:<30} ({detail['item_type'].upper()}) ${detail['price']:.2f}\n"
        
        email_body += f"""---------------------------------------------
Total Amount: ${total_amount:.2f}

Thank you for shopping with us!

Best regards,
Online Bookstore Team"""
        
        send_email(user['email'], f"Order Confirmation - Order #{order_id}", email_body)
        
        return jsonify({
            'message': 'Order created successfully',
            'order_id': order_id,
            'total_amount': total_amount
        }), 201
        
    except Exception as e:
        conn.rollback()
        return jsonify({'error': f'Order creation failed: {str(e)}'}), 500
    finally:
        cursor.close()
        conn.close()

@app.route('/api/orders', methods=['GET'])
def get_orders():
    """Get all orders (manager) or user's orders (customer)"""
    token = request.headers.get('Authorization')
    if not token or token not in sessions:
        return jsonify({'error': 'Unauthorized'}), 401
    
    user = sessions[token]
    
    conn = get_db_connection()
    if not conn:
        return jsonify({'error': 'Database connection failed'}), 500
    
    try:
        cursor = conn.cursor(dictionary=True)
        
        if user['role'] == 'manager':
            query = """SELECT o.order_id, o.user_id, u.username, o.payment_status, 
                      o.total_amount, o.created_at
                      FROM orders o JOIN users u ON o.user_id = u.user_id
                      ORDER BY o.created_at DESC"""
            cursor.execute(query)
        else:
            query = """SELECT order_id, user_id, payment_status, total_amount, created_at
                      FROM orders WHERE user_id = %s ORDER BY created_at DESC"""
            cursor.execute(query, (user['user_id'],))
        
        orders = cursor.fetchall()
        return jsonify({'orders': orders}), 200
        
    finally:
        cursor.close()
        conn.close()

@app.route('/api/orders/<int:order_id>/payment', methods=['PUT'])
def update_payment_status(order_id):
    """Update order payment status (manager only)"""
    token = request.headers.get('Authorization')
    if not token or token not in sessions:
        return jsonify({'error': 'Unauthorized'}), 401
    
    if sessions[token]['role'] != 'manager':
        return jsonify({'error': 'Forbidden'}), 403
    
    data = request.get_json()
    new_status = data.get('payment_status')
    
    if new_status not in ['Pending', 'Paid']:
        return jsonify({'error': 'Invalid payment status'}), 400
    
    conn = get_db_connection()
    if not conn:
        return jsonify({'error': 'Database connection failed'}), 500
    
    try:
        cursor = conn.cursor()
        query = "UPDATE orders SET payment_status = %s WHERE order_id = %s"
        cursor.execute(query, (new_status, order_id))
        conn.commit()
        
        if cursor.rowcount > 0:
            return jsonify({'message': 'Payment status updated'}), 200
        else:
            return jsonify({'error': 'Order not found'}), 404
        
    finally:
        cursor.close()
        conn.close()

# ============================================================================
# HEALTH CHECK
# ============================================================================

@app.route('/api/health', methods=['GET'])
def health_check():
    """Health check endpoint"""
    return jsonify({
        'status': 'healthy',
        'timestamp': datetime.now().isoformat()
    }), 200

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)