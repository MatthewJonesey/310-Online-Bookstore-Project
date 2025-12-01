Compiling: javac -d bin -cp "lib/*" src/*.java
Running: 
first run python with the > button
Java: java -cp "bin;lib/*" Application



Product Requirements Document: Online

Bookstore with Desktop Application

Version: 1.0

Date: 2024-10-20

1.  Introduction

This document outlines the product requirements for a desktop
application designed for an online business to sell and rent books. The
system will consist of a backend RESTful API built with Java or Python
Flask and a MySQL database, and a client-facing desktop GUI application
built in either Java or Python.

The primary goal is to provide a seamless experience for customers to
discover, purchase, and rent books, while also giving managers the
necessary tools to administer orders and payments.

2.  User Roles

There are two primary user roles for this system:

-   **Customer**: A registered user who wants to find and acquire books.
    They can browse the catalog, search for specific titles or authors,
    and place orders to either buy or rent books.
-   **Manager**: An administrative user responsible for business
    operations. They can log in to a specialized interface to manage
    books, view orders and update the payment status of those orders.

3.  Functional Requirements

### 3.1 User Account Management

  ------------------------------------------------------------------------
  ID          Requirement                         Details
  ----------- ----------------------------------- ------------------------
  **FR1.1**   User Registration                   A new user must be able
                                                  to create an account.
                                                  Required fields:
                                                  username, password, and
                                                  a valid email address.
                                                  Passwords must be
                                                  securely hashed on the
                                                  backend.

  **FR1.2**   User Login                          A registered user must
                                                  be able to log in using
                                                  their username and
                                                  password. The system
                                                  must authenticate the
                                                  user and establish a
                                                  session.

  **FR1.3**   Session Persistence                 Once logged in, the user
                                                  should remain
                                                  authenticated until they
                                                  explicitly log out. All
                                                  subsequent actions
                                                  requiring authentication
                                                  must be tied to their
                                                  session.
  ------------------------------------------------------------------------

### 3.2 Book Catalog and Search

  ------------------------------------------------------------------------
  ID          Requirement                         Details
  ----------- ----------------------------------- ------------------------
  **FR2.1**   Book Search                         A logged‑in user must be
                                                  able to search for
                                                  books. The search
                                                  functionality should
                                                  accept a keyword.

  **FR2.2**   Search by Title/Author              The search keyword
                                                  should be matched
                                                  against both book titles
                                                  and author names.

  **FR2.3**   Search Results Display              Search results should
                                                  display: Title, Author,
                                                  Price (buy), Price
                                                  (rent).
  ------------------------------------------------------------------------

### 3.3 Ordering and Transactions

  ------------------------------------------------------------------------
  ID          Requirement                         Details
  ----------- ----------------------------------- ------------------------
  **FR3.1**   Buy a Book                          A logged‑in user must be
                                                  able to select books to
                                                  purchase.

  **FR3.2**   Rent a Book                         A logged‑in user must be
                                                  able to select books to
                                                  rent.

  **FR3.3**   Place Order                         Users can finalize
                                                  selections for
                                                  buying/renting in one
                                                  transaction.
  ------------------------------------------------------------------------

### 3.4 Billing and Notifications

  ------------------------------------------------------------------------
  ID          Requirement                         Details
  ----------- ----------------------------------- ------------------------
  **FR4.1**   Bill Generation                     System must generate a
                                                  detailed bill with order
                                                  ID, items, prices, and
                                                  total.

  **FR4.2**   Email Notification                  Bill must be
                                                  automatically emailed to
                                                  the user.
  ------------------------------------------------------------------------

### 3.5 Manager Functions

  ------------------------------------------------------------------------
  ID          Requirement                         Details
  ----------- ----------------------------------- ------------------------
  **FR5.1**   Manager Login                       A separate, secure login
                                                  mechanism.

  **FR5.2**   View Orders                         Managers can view all
                                                  buy and rental orders.

  **FR5.3**   Update Payment Status               Managers can update an
                                                  order's payment status.

  **FR5.4**   Create/Update Book Info             Managers can add/update
                                                  books and mark returned
                                                  rentals as available.
  ------------------------------------------------------------------------

4.  Non‑Functional Requirements

### 4.1 Technology Stack

  Area         Requirement
  ------------ ----------------------------------------------
  Backend      Java or Python (Flask)
  Database     MySQL
  API Design   RESTful
  Frontend     Desktop GUI via Java Swing or Python Tkinter

### 4.2 Performance

  -------------------------------------------------------------------------
  ID           Requirement                         Details
  ------------ ----------------------------------- ------------------------
  **NFR1.1**   API Response Time                   \< 500ms typical
                                                   requests

  **NFR1.2**   GUI Responsiveness                  GUI must stay
                                                   responsive; asynchronous
                                                   backend calls required
  -------------------------------------------------------------------------

### 4.3 Security

  -------------------------------------------------------------------------
  ID           Requirement                         Details
  ------------ ----------------------------------- ------------------------
  **NFR2.1**   Password Storage                    Strong hashing (e.g.,
                                                   bcrypt)

  **NFR2.2**   API Authentication                  Protected endpoints
                                                   requiring valid auth
                                                   token

  **NFR2.3**   Authorization                       Users only access their
                                                   own data; roles
                                                   restricted
  -------------------------------------------------------------------------

5.  Assumptions and Dependencies

-   External SMTP/email service required for billing notifications.
-   Initial book database population is out of scope.
-   No real‑time payment processing needed.

6.  Future Considerations (Not in v1.0)

-   User profiles and order history\
-   Book reviews and ratings\
-   Inventory management\
-   Advanced filtering options\
-   Rental return handling
