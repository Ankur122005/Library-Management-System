-- ============================================================
-- LIBRARY MANAGEMENT SYSTEM
-- SQL INTERFACE FILE
-- Database: lms
-- ============================================================
--
-- This file contains the main SQL operations used by the
-- Library Management System.
--
-- Schema used:
-- BOOK, AUTHOR, BOOK_AUTHOR, CATEGORY, PUBLISHER,
-- LIBRARY_BRANCH, BOOK_COPIES, MEMBER, STAFF,
-- STAFF_DESIGNATION, BOOK_LOANS, FINE, RESERVATION, REVIEW
--
-- NOTE:
-- The database and tables are assumed to already exist.
-- Change the sample IDs/values at the bottom before using
-- INSERT/UPDATE/DELETE operations on actual data.
-- ============================================================

USE lms;


-- ============================================================
-- 1. BOOK OPERATIONS
-- ============================================================

-- 1.1 View all books
SELECT
    b.book_id,
    b.title,
    b.isbn,
    c.category_name,
    p.name AS publisher,
    b.language,
    b.edition,
    b.publication_year,
    b.price
FROM BOOK b
         LEFT JOIN CATEGORY c
                   ON b.category_id = c.category_id
         LEFT JOIN PUBLISHER p
                   ON b.publisher_id = p.publisher_id
ORDER BY b.book_id;


-- 1.2 Search book by title
-- Change 'database' to the title/keyword to search.
SELECT
    b.book_id,
    b.title,
    b.isbn,
    c.category_name,
    b.language,
    b.edition,
    b.publication_year
FROM BOOK b
         LEFT JOIN CATEGORY c
                   ON b.category_id = c.category_id
WHERE b.title LIKE '%database%';


-- 1.3 Search book by ISBN
-- Change the ISBN value as required.
SELECT *
FROM BOOK
WHERE isbn = '9780000000000';


-- 1.4 View available books
SELECT
    b.book_id,
    b.title,
    bc.branch_id,
    bc.copies_available
FROM BOOK b
         JOIN BOOK_COPIES bc
              ON b.book_id = bc.book_id
WHERE bc.copies_available > 0
ORDER BY b.title;


-- 1.5 Add a book
-- Change the values before executing.
INSERT INTO BOOK
(book_id, title, isbn, category_id, publisher_id,
 language, edition, publication_year, price)
VALUES
    (101, 'Database System Concepts', '9780000000000',
     1, 1, 'English', '7th', 2019, 850.00);


-- 1.6 Update book price
UPDATE BOOK
SET price = 900.00
WHERE book_id = 101;


-- 1.7 Delete a book
-- Execute only when the book is not referenced by
-- BOOK_AUTHOR, BOOK_COPIES, RESERVATION or REVIEW.
DELETE FROM BOOK
WHERE book_id = 101;


-- ============================================================
-- 2. AUTHOR OPERATIONS
-- ============================================================

-- 2.1 View authors
SELECT
    author_id,
    fname,
    minit,
    lname
FROM AUTHOR
ORDER BY lname, fname;


-- 2.2 View books with their authors
SELECT
    b.book_id,
    b.title,
    CONCAT(a.fname, ' ',
           IF(a.minit IS NULL OR a.minit = '', '', CONCAT(a.minit, '. ')),
           a.lname) AS author_name
FROM BOOK b
         JOIN BOOK_AUTHOR ba
              ON b.book_id = ba.book_id
         JOIN AUTHOR a
              ON ba.author_id = a.author_id
ORDER BY b.title, author_name;


-- ============================================================
-- 3. CATEGORY OPERATIONS
-- ============================================================

-- 3.1 View all categories
SELECT
    category_id,
    category_name,
    description
FROM CATEGORY
ORDER BY category_name;


-- 3.2 Count books in each category
SELECT
    c.category_name,
    COUNT(b.book_id) AS total_books
FROM CATEGORY c
         LEFT JOIN BOOK b
                   ON c.category_id = b.category_id
GROUP BY c.category_id, c.category_name
ORDER BY c.category_name;


-- ============================================================
-- 4. MEMBER OPERATIONS
-- ============================================================

-- 4.1 View all members
SELECT
    card_no,
    fname,
    lname,
    phone,
    email,
    campus_id,
    registration_date,
    expiry_date,
    status
FROM MEMBER
ORDER BY card_no;


-- 4.2 Find a member by card number
SELECT *
FROM MEMBER
WHERE card_no = 1001;


-- 4.3 View active members
SELECT
    card_no,
    CONCAT(fname, ' ', lname) AS member_name,
    phone,
    email,
    expiry_date
FROM MEMBER
WHERE status = 'Active'
ORDER BY lname, fname;


-- 4.4 Add a member
-- Change the values before executing.
INSERT INTO MEMBER
(card_no, fname, lname, address, phone, email,
 campus_id, registration_date, expiry_date, status)
VALUES
    (1001, 'Ankur', 'Mishra', 'Bhopal',
     '9876543210', 'ankur@example.com',
     'VIT001', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 YEAR), 'Active');


-- 4.5 Update member phone number
UPDATE MEMBER
SET phone = '9999999999'
WHERE card_no = 1001;


-- 4.6 Search member by name
SELECT
    card_no,
    CONCAT(fname, ' ', lname) AS member_name,
    phone,
    email,
    status
FROM MEMBER
WHERE fname LIKE '%Ankur%'
   OR lname LIKE '%Mishra%';


-- ============================================================
-- 5. BOOK COPIES / BRANCH OPERATIONS
-- ============================================================

-- 5.1 View copies available at each branch
SELECT
    bc.copy_id,
    b.title,
    lb.branch_name,
    bc.no_of_copies,
    bc.copies_available,
    bc.rental_category,
    bc.rental_price
FROM BOOK_COPIES bc
         JOIN BOOK b
              ON bc.book_id = b.book_id
         JOIN LIBRARY_BRANCH lb
              ON bc.branch_id = lb.branch_id
ORDER BY lb.branch_name, b.title;


-- 5.2 Find a book across branches
SELECT
    b.title,
    lb.branch_name,
    bc.copies_available
FROM BOOK b
         JOIN BOOK_COPIES bc
              ON b.book_id = bc.book_id
         JOIN LIBRARY_BRANCH lb
              ON bc.branch_id = lb.branch_id
WHERE b.title LIKE '%database%';


-- ============================================================
-- 6. ISSUE / CIRCULATION OPERATIONS
-- ============================================================

-- 6.1 View currently issued books
SELECT
    bl.loan_id,
    m.card_no,
    CONCAT(m.fname, ' ', m.lname) AS member_name,
    b.title,
    lb.branch_name,
    bl.date_out,
    bl.due_date,
    bl.lend_type
FROM BOOK_LOANS bl
         JOIN MEMBER m
              ON bl.card_no = m.card_no
         JOIN BOOK_COPIES bc
              ON bl.copy_id = bc.copy_id
         JOIN BOOK b
              ON bc.book_id = b.book_id
         JOIN LIBRARY_BRANCH lb
              ON bc.branch_id = lb.branch_id
WHERE bl.date_in IS NULL
ORDER BY bl.due_date;


-- 6.2 View a member's borrowing history
-- Change 1001 to the required card number.
SELECT
    bl.loan_id,
    b.title,
    bl.date_out,
    bl.due_date,
    bl.date_in,
    bl.lend_type
FROM BOOK_LOANS bl
         JOIN BOOK_COPIES bc
              ON bl.copy_id = bc.copy_id
         JOIN BOOK b
              ON bc.book_id = b.book_id
WHERE bl.card_no = 1001
ORDER BY bl.date_out DESC;


-- 6.3 Issue a book
-- Change the sample IDs before executing.
START TRANSACTION;

INSERT INTO BOOK_LOANS
(loan_id, copy_id, card_no, issued_by_staff,
 date_out, due_date, lend_type, grace_period)
VALUES
    (5001, 101, 1001, 1,
     CURDATE(),
     DATE_ADD(CURDATE(), INTERVAL 14 DAY),
     'Standard',
     2);

UPDATE BOOK_COPIES
SET copies_available = copies_available - 1
WHERE copy_id = 101
  AND copies_available > 0;

COMMIT;


-- 6.4 Return a book
-- Change loan_id and copy_id before executing.
START TRANSACTION;

UPDATE BOOK_LOANS
SET date_in = CURDATE()
WHERE loan_id = 5001
  AND date_in IS NULL;

UPDATE BOOK_COPIES
SET copies_available = copies_available + 1
WHERE copy_id = 101;

COMMIT;


-- 6.5 Find overdue books
SELECT
    bl.loan_id,
    m.card_no,
    CONCAT(m.fname, ' ', m.lname) AS member_name,
    b.title,
    bl.date_out,
    bl.due_date,
    DATEDIFF(CURDATE(), bl.due_date) AS days_overdue
FROM BOOK_LOANS bl
         JOIN MEMBER m
              ON bl.card_no = m.card_no
         JOIN BOOK_COPIES bc
              ON bl.copy_id = bc.copy_id
         JOIN BOOK b
              ON bc.book_id = b.book_id
WHERE bl.date_in IS NULL
  AND bl.due_date < CURDATE()
ORDER BY bl.due_date;


-- ============================================================
-- 7. FINE OPERATIONS
-- ============================================================

-- 7.1 View unpaid fines
SELECT
    f.fine_id,
    f.loan_id,
    m.card_no,
    CONCAT(m.fname, ' ', m.lname) AS member_name,
    b.title,
    f.amount,
    f.reason,
    f.paid_status
FROM FINE f
         JOIN BOOK_LOANS bl
              ON f.loan_id = bl.loan_id
         JOIN MEMBER m
              ON bl.card_no = m.card_no
         JOIN BOOK_COPIES bc
              ON bl.copy_id = bc.copy_id
         JOIN BOOK b
              ON bc.book_id = b.book_id
WHERE f.paid_status = 'Unpaid'
ORDER BY f.fine_id;


-- 7.2 Add a fine
INSERT INTO FINE
(fine_id, loan_id, amount, reason, paid_status)
VALUES
    (1, 5001, 50.00, 'Late return', 'Unpaid');


-- 7.3 Mark a fine as paid
UPDATE FINE
SET paid_status = 'Paid',
    paid_date = CURDATE()
WHERE fine_id = 1;


-- ============================================================
-- 8. RESERVATION OPERATIONS
-- ============================================================

-- 8.1 View pending reservations
SELECT
    r.reservation_id,
    b.title,
    m.card_no,
    CONCAT(m.fname, ' ', m.lname) AS member_name,
    r.reservation_date,
    r.status,
    r.expiry_date
FROM RESERVATION r
         JOIN BOOK b
              ON r.book_id = b.book_id
         JOIN MEMBER m
              ON r.card_no = m.card_no
WHERE r.status = 'Pending'
ORDER BY r.reservation_date;


-- 8.2 Add a reservation
INSERT INTO RESERVATION
(reservation_id, book_id, card_no, reservation_date, status, expiry_date)
VALUES
    (1, 101, 1001, CURDATE(), 'Pending',
     DATE_ADD(CURDATE(), INTERVAL 3 DAY));


-- 8.3 Cancel a reservation
UPDATE RESERVATION
SET status = 'Cancelled'
WHERE reservation_id = 1;


-- ============================================================
-- 9. REVIEW OPERATIONS
-- ============================================================

-- 9.1 View reviews for a book
SELECT
    r.review_id,
    b.title,
    m.card_no,
    CONCAT(m.fname, ' ', m.lname) AS member_name,
    r.rating,
    r.comment,
    r.review_date
FROM REVIEW r
         JOIN BOOK b
              ON r.book_id = b.book_id
         JOIN MEMBER m
              ON r.card_no = m.card_no
WHERE b.book_id = 101
ORDER BY r.review_date DESC;


-- 9.2 Add a review
INSERT INTO REVIEW
(review_id, book_id, card_no, rating, comment, review_date)
VALUES
    (1, 101, 1001, 5, 'Very useful book.', CURDATE());


-- 9.3 Average rating for each book
SELECT
    b.book_id,
    b.title,
    ROUND(AVG(r.rating), 2) AS average_rating,
    COUNT(r.review_id) AS total_reviews
FROM BOOK b
         LEFT JOIN REVIEW r
                   ON b.book_id = r.book_id
GROUP BY b.book_id, b.title
ORDER BY average_rating DESC;


-- ============================================================
-- 10. REPORTS / DBMS CONCEPTS
-- ============================================================

-- 10.1 Number of books in each category
SELECT
    c.category_name,
    COUNT(b.book_id) AS total_books
FROM CATEGORY c
         LEFT JOIN BOOK b
                   ON c.category_id = b.category_id
GROUP BY c.category_id, c.category_name;


-- 10.2 Most borrowed books
SELECT
    b.book_id,
    b.title,
    COUNT(bl.loan_id) AS times_borrowed
FROM BOOK_LOANS bl
         JOIN BOOK_COPIES bc
              ON bl.copy_id = bc.copy_id
         JOIN BOOK b
              ON bc.book_id = b.book_id
GROUP BY b.book_id, b.title
ORDER BY times_borrowed DESC;


-- 10.3 Total number of books
SELECT COUNT(*) AS total_books
FROM BOOK;


-- 10.4 Total available copies
SELECT SUM(copies_available) AS total_available_copies
FROM BOOK_COPIES;


-- 10.5 Members with active loans
SELECT
    m.card_no,
    CONCAT(m.fname, ' ', m.lname) AS member_name,
    COUNT(bl.loan_id) AS active_loans
FROM MEMBER m
         JOIN BOOK_LOANS bl
              ON m.card_no = bl.card_no
WHERE bl.date_in IS NULL
GROUP BY m.card_no, m.fname, m.lname
ORDER BY active_loans DESC;


-- ============================================================
-- END OF SQL INTERFACE
-- ============================================================
