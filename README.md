# 📚 Library Management System

A simple **Library Management System** developed as a DBMS academic
project using **Java, MySQL and JDBC**.

The system manages books, authors, members, staff and book circulation
records. The project demonstrates relational database design, ER
modeling, keys, relationships, normalization and SQL-based database
operations.

------------------------------------------------------------------------

## 📌 Project Overview

The Library Management System is a console-based application connected
to a MySQL database.

The main purpose of the project is to provide basic library operations
while demonstrating important DBMS concepts through a properly
structured relational database.

### Main Features

-   View books
-   Search books
-   Add books
-   View members
-   Add members
-   Issue books
-   Return books
-   View issued books
-   View overdue books
-   Maintain book, member and circulation information

------------------------------------------------------------------------

## 🛠️ Technologies Used

| Technology | Purpose |
| --- | --- |
| Java | Application development |
| MySQL | Database management |
| JDBC | Java-MySQL connectivity |
| Maven | Project and dependency management |
| IntelliJ IDEA | Development |
| Git & GitHub | Version control |

------------------------------------------------------------------------

# 🗄️ Database Schema

The database used by the project is:

``` text
Database: lms
```

The database is organized into separate related tables instead of
storing all library information in one table. This helps reduce data
redundancy and makes the database easier to maintain.

## Tables

| Table | Purpose |
| --- | --- |
| `BOOK` | Stores book information |
| `AUTHOR` | Stores author information |
| `BOOK_AUTHOR` | Connects books and authors |
| `CATEGORY` | Stores book categories |
| `PUBLISHER` | Stores publisher information |
| `LIBRARY_BRANCH` | Stores library branch information |
| `BOOK_COPIES` | Stores physical book copy information |
| `MEMBER` | Stores member information |
| `STAFF` | Stores staff information |
| `STAFF_DESIGNATION` | Stores staff designation and access level |
| `BOOK_LOANS` | Stores book issue and return records |
| `FINE` | Stores fine information |
| `RESERVATION` | Stores book reservations |
| `REVIEW` | Stores member reviews |

## Table Definitions

### BOOK

| Column | Type | Key / Description |
| --- | --- | --- |
| `book_id` | INT | Primary Key |
| `title` | VARCHAR(200) | Book title |
| `isbn` | VARCHAR(20) | Unique ISBN |
| `category_id` | INT | FK → CATEGORY |
| `publisher_id` | INT | FK → PUBLISHER |
| `language` | VARCHAR(30) | Book language |
| `edition` | VARCHAR(20) | Book edition |
| `publication_year` | YEAR | Publication year |
| `price` | DECIMAL(8,2) | Book price |

### AUTHOR

| Column | Type | Key / Description |
| --- | --- | --- |
| `author_id` | INT | Primary Key |
| `fname` | VARCHAR(50) | First name |
| `minit` | CHAR(1) | Middle initial, nullable |
| `lname` | VARCHAR(50) | Last name |

### BOOK_AUTHOR

Junction table used to represent the many-to-many relationship between
books and authors.

| Column | Type | Key / Description |
| --- | --- | --- |
| `book_id` | INT | PK, FK → BOOK |
| `author_id` | INT | PK, FK → AUTHOR |

### CATEGORY

| Column | Type | Key / Description |
| --- | --- | --- |
| `category_id` | INT | Primary Key |
| `category_name` | VARCHAR(60) | Category name |
| `description` | VARCHAR(255) | Optional description |

### PUBLISHER

| Column | Type | Key / Description |
| --- | --- | --- |
| `publisher_id` | INT | Primary Key |
| `name` | VARCHAR(100) | Publisher name |
| `address` | VARCHAR(200) | Publisher address |
| `contact_no` | VARCHAR(15) | Contact number |

### LIBRARY_BRANCH

| Column | Type | Key / Description |
| --- | --- | --- |
| `branch_id` | INT | Primary Key |
| `branch_name` | VARCHAR(80) | Branch name |
| `address` | VARCHAR(200) | Address |
| `city` | VARCHAR(50) | City |
| `state` | VARCHAR(50) | State |
| `zip_code` | VARCHAR(10) | Postal code |
| `contact_no` | VARCHAR(15) | Contact number |
| `manager_id` | INT | FK → STAFF |

### BOOK_COPIES

| Column | Type | Key / Description |
| --- | --- | --- |
| `copy_id` | INT | Primary Key |
| `book_id` | INT | FK → BOOK |
| `branch_id` | INT | FK → LIBRARY_BRANCH |
| `no_of_copies` | INT | Total copies |
| `copies_available` | INT | Available copies |
| `rental_category` | ENUM | Rentable / Not_Rentable |
| `rental_price` | DECIMAL(6,2) | Rental price, nullable |

### MEMBER

| Column | Type | Key / Description |
| --- | --- | --- |
| `card_no` | INT | Primary Key |
| `fname` | VARCHAR(50) | First name |
| `lname` | VARCHAR(50) | Last name |
| `address` | VARCHAR(200) | Address |
| `phone` | VARCHAR(15) | Phone number |
| `email` | VARCHAR(100) | Email |
| `campus_id` | VARCHAR(20) | Optional campus ID |
| `registration_date` | DATE | Registration date |
| `expiry_date` | DATE | Membership expiry |
| `status` | ENUM | Active / Expired / Suspended |

### STAFF

| Column | Type | Key / Description |
| --- | --- | --- |
| `staff_id` | INT | Primary Key |
| `name` | VARCHAR(80) | Staff name |
| `designation_id` | INT | FK → STAFF_DESIGNATION |
| `branch_id` | INT | FK → LIBRARY_BRANCH |
| `contact_no` | VARCHAR(15) | Contact number |
| `username` | VARCHAR(30) | System username |
| `password_hash` | VARCHAR(255) | Password hash |

### STAFF_DESIGNATION

| Column | Type | Key / Description |
| --- | --- | --- |
| `designation_id` | INT | Primary Key |
| `designation_type` | VARCHAR(40) | Designation |
| `access_level` | TINYINT | Access level |

### BOOK_LOANS

Stores book issue and return/circulation records.

| Column | Type | Key / Description |
| --- | --- | --- |
| `loan_id` | INT | Primary Key |
| `copy_id` | INT | FK → BOOK_COPIES |
| `card_no` | INT | FK → MEMBER |
| `issued_by_staff` | INT | FK → STAFF |
| `date_out` | DATE | Issue date |
| `due_date` | DATE | Due date |
| `date_in` | DATE | Return date, nullable |
| `lend_type` | VARCHAR(20) | Lending type |
| `grace_period` | INT | Grace period in days, nullable |

### FINE

| Column | Type | Key / Description |
| --- | --- | --- |
| `fine_id` | INT | Primary Key |
| `loan_id` | INT | FK → BOOK_LOANS |
| `amount` | DECIMAL(6,2) | Fine amount |
| `reason` | VARCHAR(100) | Reason for fine |
| `paid_status` | ENUM | Paid / Unpaid |
| `paid_date` | DATE | Payment date, nullable |

### RESERVATION

| Column | Type | Key / Description |
| --- | --- | --- |
| `reservation_id` | INT | Primary Key |
| `book_id` | INT | FK → BOOK |
| `card_no` | INT | FK → MEMBER |
| `reservation_date` | DATE | Reservation date |
| `status` | ENUM | Pending / Fulfilled / Cancelled |
| `expiry_date` | DATE | Reservation expiry |

### REVIEW

| Column | Type | Key / Description |
| --- | --- | --- |
| `review_id` | INT | Primary Key |
| `book_id` | INT | FK → BOOK |
| `card_no` | INT | FK → MEMBER |
| `rating` | TINYINT | Rating from 1--5 |
| `comment` | VARCHAR(255) | Optional comment |
| `review_date` | DATE | Review date |

------------------------------------------------------------------------

# 🖼️ ER Diagram

The ER diagram represents the entities in the library system and the
relationships between them.

The following diagram can be rendered directly by GitHub using Mermaid:

``` mermaid
erDiagram
    CATEGORY ||--o{ BOOK : contains
    PUBLISHER ||--o{ BOOK : publishes
    BOOK ||--o{ BOOK_AUTHOR : has
    AUTHOR ||--o{ BOOK_AUTHOR : writes

    LIBRARY_BRANCH ||--o{ BOOK_COPIES : stores
    BOOK ||--o{ BOOK_COPIES : has

    STAFF_DESIGNATION ||--o{ STAFF : assigns
    LIBRARY_BRANCH ||--o{ STAFF : employs

    MEMBER ||--o{ BOOK_LOANS : makes
    STAFF ||--o{ BOOK_LOANS : issues
    BOOK_COPIES ||--o{ BOOK_LOANS : included_in

    BOOK_LOANS ||--o{ FINE : generates

    BOOK ||--o{ RESERVATION : reserved
    MEMBER ||--o{ RESERVATION : makes

    BOOK ||--o{ REVIEW : receives
    MEMBER ||--o{ REVIEW : writes

    CATEGORY {
        INT category_id PK
        VARCHAR category_name
        VARCHAR description
    }

    PUBLISHER {
        INT publisher_id PK
        VARCHAR name
        VARCHAR address
        VARCHAR contact_no
    }

    BOOK {
        INT book_id PK
        VARCHAR title
        VARCHAR isbn UK
        INT category_id FK
        INT publisher_id FK
        VARCHAR language
        VARCHAR edition
        YEAR publication_year
        DECIMAL price
    }

    AUTHOR {
        INT author_id PK
        VARCHAR fname
        CHAR minit
        VARCHAR lname
    }

    BOOK_AUTHOR {
        INT book_id PK, FK
        INT author_id PK, FK
    }

    LIBRARY_BRANCH {
        INT branch_id PK
        VARCHAR branch_name
        VARCHAR address
        VARCHAR city
        VARCHAR state
        VARCHAR zip_code
        VARCHAR contact_no
        INT manager_id FK
    }

    BOOK_COPIES {
        INT copy_id PK
        INT book_id FK
        INT branch_id FK
        INT no_of_copies
        INT copies_available
        ENUM rental_category
        DECIMAL rental_price
    }

    MEMBER {
        INT card_no PK
        VARCHAR fname
        VARCHAR lname
        VARCHAR address
        VARCHAR phone
        VARCHAR email
        VARCHAR campus_id
        DATE registration_date
        DATE expiry_date
        ENUM status
    }

    STAFF {
        INT staff_id PK
        VARCHAR name
        INT designation_id FK
        INT branch_id FK
        VARCHAR contact_no
        VARCHAR username
        VARCHAR password_hash
    }

    STAFF_DESIGNATION {
        INT designation_id PK
        VARCHAR designation_type
        TINYINT access_level
    }

    BOOK_LOANS {
        INT loan_id PK
        INT copy_id FK
        INT card_no FK
        INT issued_by_staff FK
        DATE date_out
        DATE due_date
        DATE date_in
        VARCHAR lend_type
        INT grace_period
    }

    FINE {
        INT fine_id PK
        INT loan_id FK
        DECIMAL amount
        VARCHAR reason
        ENUM paid_status
        DATE paid_date
    }

    RESERVATION {
        INT reservation_id PK
        INT book_id FK
        INT card_no FK
        DATE reservation_date
        ENUM status
        DATE expiry_date
    }

    REVIEW {
        INT review_id PK
        INT book_id FK
        INT card_no FK
        TINYINT rating
        VARCHAR comment
        DATE review_date
    }
```

------------------------------------------------------------------------

# 🧠 DBMS Concepts Used

## 1. Relational Database

The project uses a relational database in MySQL. Information is divided
into related tables such as `BOOK`, `AUTHOR`, `MEMBER`, `CATEGORY` and
`BOOK_LOANS`.

This avoids keeping all library information in one large table and helps
reduce data redundancy.

## 2. Primary Key

A primary key uniquely identifies a record in a table.

Examples:

``` text
BOOK       → book_id
AUTHOR     → author_id
MEMBER     → card_no
BOOK_LOANS → loan_id
```

## 3. Foreign Key

A foreign key connects one table with another.

For example:

``` text
BOOK.category_id
       ↓
CATEGORY.category_id
```

This establishes a relationship between books and categories.

## 4. Composite Key

`BOOK_AUTHOR` uses both `book_id` and `author_id` as its composite
primary key.

``` text
BOOK_AUTHOR
------------
book_id
author_id
```

This represents the many-to-many relationship between books and authors.

## 5. Relationships and Cardinality

The database mainly uses one-to-many relationships.

Examples:

``` text
CATEGORY  1 ───── N  BOOK

PUBLISHER 1 ───── N  BOOK

MEMBER    1 ───── N  BOOK_LOANS

STAFF     1 ───── N  BOOK_LOANS
```

`BOOK` and `AUTHOR` have a many-to-many relationship, which is resolved
through `BOOK_AUTHOR`.

## 6. Normalization

The database uses normalization to reduce redundancy and improve
consistency.

### 1NF

Each field contains an atomic value and there are no repeating groups.

### 2NF

Partial dependencies are avoided, particularly in tables with composite
keys such as `BOOK_AUTHOR`.

### 3NF

Non-key information is separated into appropriate tables. For example,
category details are stored in `CATEGORY` and `BOOK` stores only
`category_id`.

## 7. SQL

SQL is used to retrieve and modify information in the database.

The project uses SQL for operations such as:

-   Viewing books
-   Searching books
-   Adding books
-   Managing members
-   Issuing books
-   Returning books
-   Finding overdue books

## 8. JOIN

JOIN is used when information from multiple related tables is required.

For example, book information can be combined with category and
publisher information using JOIN operations.

## 9. Constraints

The database uses constraints to maintain valid data.

Important constraints include:

``` text
PRIMARY KEY
FOREIGN KEY
UNIQUE
```

For example, the ISBN of a book is unique.

## 10. JDBC

JDBC (Java Database Connectivity) is used to connect the Java
application with MySQL.

``` text
Java Application
       ↓
      JDBC
       ↓
     MySQL
       ↓
      lms
```

The database connection is handled through `DBConnection.java`.

------------------------------------------------------------------------

# 💻 Application Structure

``` text
User
 │
 ▼
Main.java
 │
 ├── Book Management
 ├── Member Management
 ├── Issue Book
 ├── Return Book
 ├── View Issued Books
 └── Overdue Books
 │
 ▼
DBConnection.java
 │
 ▼
MySQL Database
```

------------------------------------------------------------------------

# 📋 Console Menu

The application uses a simple number-based console interface:

``` text
========================================
       LIBRARY MANAGEMENT SYSTEM
========================================

1. Books
2. Members
3. Issue Book
4. Return Book
5. View Issued Books
6. Overdue Books
7. Exit
```

This keeps the application simple and focuses on the database
functionality.

------------------------------------------------------------------------

# 🔄 Book Issue and Return

### Issue Book

When a book is issued:

1.  The book is selected.
2.  The member is identified using the card number.
3.  Availability is checked.
4.  A loan record is created.
5.  The issue date and due date are stored.
6.  The staff member issuing the book is recorded.

### Return Book

When a book is returned:

1.  The active loan is identified.
2.  The return date is recorded.
3.  Book availability is updated.
4.  The circulation history remains stored.

------------------------------------------------------------------------

# ▶️ How to Run

## Prerequisites

-   Java JDK
-   MySQL
-   IntelliJ IDEA
-   Maven

## 1. Create Database

``` sql
CREATE DATABASE lms;
```

Then:

``` sql
USE lms;
```

## 2. Create the Tables

Run the SQL file:

``` text
sql/library_queries.sql
```

## 3. Configure Database Connection

Update the MySQL username and password in:

``` text
src/main/java/com/hms/DBConnection.java
```

## 4. Run the Application

Run the `Main` class from IntelliJ IDEA.

On Windows, the application can also be started using:

``` text
run_library.bat
```

------------------------------------------------------------------------

# 📁 Project Structure

``` text
Library-Management-System/
│
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── hms/
│                   ├── Main.java
│                   └── DBConnection.java
│
├── sql/
│   └── library_queries.sql
│
├── pom.xml
├── run_library.bat
├── .gitignore
└── README.md
```

------------------------------------------------------------------------

# 🎯 Project Objectives

-   Design a relational database for a library.
-   Apply normalization to reduce data redundancy.
-   Establish relationships using primary and foreign keys.
-   Implement basic library operations using SQL.
-   Connect a Java application to MySQL using JDBC.
-   Demonstrate practical DBMS concepts through a working application.

------------------------------------------------------------------------

# 🚀 Future Scope

The system can be extended with:

-   Graphical user interface
-   Automatic fine calculation
-   Book reservation notifications
-   Email notifications
-   Advanced search
-   Member borrowing history
-   Reports and statistics
-   Additional library branches

------------------------------------------------------------------------

## 📚 Project

**Library Management System**

Developed as a DBMS academic project using Java, MySQL and JDBC.

------------------------------------------------------------------------
