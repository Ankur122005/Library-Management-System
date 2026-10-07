-- ============================================================
--  Library Management System  (DBMS Lab Project)
--  File    : sql/lms_setup.sql
--  Purpose : 1) Create database `lms`
--            2) Create all 14 tables (PK / FK / UNIQUE / CHECK)
--            3) Insert sample data into every table
--            4) Show the output (row counts + SELECT * on each table)
--  Run     : mysql -u root -p < sql/lms_setup.sql
--            (or open in MySQL Workbench and execute the whole script)
--  Note    : Running this script again DROPS and recreates the tables.
--  Note    : All data below is sample data for demonstration.
-- ============================================================

-- ------------------------------------------------------------
-- 1. CREATE DATABASE
-- ------------------------------------------------------------
CREATE DATABASE IF NOT EXISTS lms;
USE lms;

-- Drop old tables (children first) so the script can be re-run
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS REVIEW;
DROP TABLE IF EXISTS RESERVATION;
DROP TABLE IF EXISTS FINE;
DROP TABLE IF EXISTS BOOK_LOANS;
DROP TABLE IF EXISTS BOOK_COPIES;
DROP TABLE IF EXISTS BOOK_AUTHOR;
DROP TABLE IF EXISTS BOOK;
DROP TABLE IF EXISTS STAFF;
DROP TABLE IF EXISTS LIBRARY_BRANCH;
DROP TABLE IF EXISTS STAFF_DESIGNATION;
DROP TABLE IF EXISTS MEMBER;
DROP TABLE IF EXISTS AUTHOR;
DROP TABLE IF EXISTS PUBLISHER;
DROP TABLE IF EXISTS CATEGORY;
SET FOREIGN_KEY_CHECKS = 1;

-- ------------------------------------------------------------
-- 2. CREATE TABLES
-- ------------------------------------------------------------

CREATE TABLE CATEGORY (
    category_id   INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(60)  NOT NULL UNIQUE,
    description   VARCHAR(255)
);

CREATE TABLE PUBLISHER (
    publisher_id INT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    address      VARCHAR(200),
    contact_no   VARCHAR(15)
);

CREATE TABLE AUTHOR (
    author_id INT AUTO_INCREMENT PRIMARY KEY,
    fname     VARCHAR(50) NOT NULL,
    minit     CHAR(1),
    lname     VARCHAR(50) NOT NULL
);

CREATE TABLE BOOK (
    book_id          INT AUTO_INCREMENT PRIMARY KEY,
    title            VARCHAR(200) NOT NULL,
    isbn             VARCHAR(20)  NOT NULL UNIQUE,
    category_id      INT NOT NULL,
    publisher_id     INT NOT NULL,
    language         VARCHAR(30),
    edition          VARCHAR(20),
    publication_year YEAR,
    price            DECIMAL(8,2),
    CONSTRAINT fk_book_category  FOREIGN KEY (category_id)  REFERENCES CATEGORY(category_id),
    CONSTRAINT fk_book_publisher FOREIGN KEY (publisher_id) REFERENCES PUBLISHER(publisher_id)
);

CREATE TABLE BOOK_AUTHOR (
    book_id   INT NOT NULL,
    author_id INT NOT NULL,
    PRIMARY KEY (book_id, author_id),
    CONSTRAINT fk_ba_book   FOREIGN KEY (book_id)   REFERENCES BOOK(book_id)     ON DELETE CASCADE,
    CONSTRAINT fk_ba_author FOREIGN KEY (author_id) REFERENCES AUTHOR(author_id) ON DELETE CASCADE
);

CREATE TABLE STAFF_DESIGNATION (
    designation_id   INT AUTO_INCREMENT PRIMARY KEY,
    designation_type VARCHAR(40) NOT NULL,
    access_level     TINYINT NOT NULL
);

-- manager_id references STAFF, and STAFF references LIBRARY_BRANCH (circular).
-- The manager foreign key is therefore added after STAFF is created and loaded.
CREATE TABLE LIBRARY_BRANCH (
    branch_id   INT AUTO_INCREMENT PRIMARY KEY,
    branch_name VARCHAR(80) NOT NULL,
    address     VARCHAR(200),
    city        VARCHAR(50),
    state       VARCHAR(50),
    zip_code    VARCHAR(10),
    contact_no  VARCHAR(15),
    manager_id  INT NULL
);

CREATE TABLE STAFF (
    staff_id       INT AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(80) NOT NULL,
    designation_id INT NOT NULL,
    branch_id      INT NOT NULL,
    contact_no     VARCHAR(15),
    username       VARCHAR(30) NOT NULL UNIQUE,
    password_hash  VARCHAR(255) NOT NULL,
    CONSTRAINT fk_staff_designation FOREIGN KEY (designation_id) REFERENCES STAFF_DESIGNATION(designation_id),
    CONSTRAINT fk_staff_branch      FOREIGN KEY (branch_id)      REFERENCES LIBRARY_BRANCH(branch_id)
);

CREATE TABLE BOOK_COPIES (
    copy_id          INT AUTO_INCREMENT PRIMARY KEY,
    book_id          INT NOT NULL,
    branch_id        INT NOT NULL,
    no_of_copies     INT NOT NULL,
    copies_available INT NOT NULL,
    rental_category  ENUM('Rentable','Not_Rentable') NOT NULL,
    rental_price     DECIMAL(6,2) NULL,
    CONSTRAINT chk_copies_avail CHECK (copies_available >= 0 AND copies_available <= no_of_copies),
    CONSTRAINT fk_copies_book   FOREIGN KEY (book_id)   REFERENCES BOOK(book_id),
    CONSTRAINT fk_copies_branch FOREIGN KEY (branch_id) REFERENCES LIBRARY_BRANCH(branch_id)
);

CREATE TABLE MEMBER (
    card_no           INT AUTO_INCREMENT PRIMARY KEY,
    fname             VARCHAR(50) NOT NULL,
    lname             VARCHAR(50) NOT NULL,
    address           VARCHAR(200),
    phone             VARCHAR(15),
    email             VARCHAR(100),
    campus_id         VARCHAR(20),
    registration_date DATE NOT NULL,
    expiry_date       DATE NOT NULL,
    status            ENUM('Active','Expired','Suspended') NOT NULL DEFAULT 'Active'
) AUTO_INCREMENT = 1001;

CREATE TABLE BOOK_LOANS (
    loan_id         INT AUTO_INCREMENT PRIMARY KEY,
    copy_id         INT NOT NULL,
    card_no         INT NOT NULL,
    issued_by_staff INT NOT NULL,
    date_out        DATE NOT NULL,
    due_date        DATE NOT NULL,
    date_in         DATE NULL,
    lend_type       VARCHAR(20),
    grace_period    INT NULL,
    CONSTRAINT fk_loan_copy   FOREIGN KEY (copy_id)         REFERENCES BOOK_COPIES(copy_id),
    CONSTRAINT fk_loan_member FOREIGN KEY (card_no)         REFERENCES MEMBER(card_no),
    CONSTRAINT fk_loan_staff  FOREIGN KEY (issued_by_staff) REFERENCES STAFF(staff_id)
);

CREATE TABLE FINE (
    fine_id     INT AUTO_INCREMENT PRIMARY KEY,
    loan_id     INT NOT NULL,
    amount      DECIMAL(6,2) NOT NULL,
    reason      VARCHAR(100),
    paid_status ENUM('Paid','Unpaid') NOT NULL DEFAULT 'Unpaid',
    paid_date   DATE NULL,
    CONSTRAINT fk_fine_loan FOREIGN KEY (loan_id) REFERENCES BOOK_LOANS(loan_id)
);

CREATE TABLE RESERVATION (
    reservation_id   INT AUTO_INCREMENT PRIMARY KEY,
    book_id          INT NOT NULL,
    card_no          INT NOT NULL,
    reservation_date DATE NOT NULL,
    status           ENUM('Pending','Fulfilled','Cancelled') NOT NULL DEFAULT 'Pending',
    expiry_date      DATE,
    CONSTRAINT fk_res_book   FOREIGN KEY (book_id) REFERENCES BOOK(book_id),
    CONSTRAINT fk_res_member FOREIGN KEY (card_no) REFERENCES MEMBER(card_no)
);

CREATE TABLE REVIEW (
    review_id   INT AUTO_INCREMENT PRIMARY KEY,
    book_id     INT NOT NULL,
    card_no     INT NOT NULL,
    rating      TINYINT NOT NULL,
    comment     VARCHAR(255),
    review_date DATE,
    CONSTRAINT chk_rating CHECK (rating BETWEEN 1 AND 5),
    CONSTRAINT fk_review_book   FOREIGN KEY (book_id) REFERENCES BOOK(book_id),
    CONSTRAINT fk_review_member FOREIGN KEY (card_no) REFERENCES MEMBER(card_no)
);

-- ------------------------------------------------------------
-- 3. INSERT SAMPLE DATA
-- ------------------------------------------------------------

-- CATEGORY (6 rows)
INSERT INTO CATEGORY (category_id, category_name, description) VALUES
(1, 'Computer Science', 'Programming, databases, algorithms and software engineering'),
(2, 'Fiction',          'Novels and literary fiction'),
(3, 'Science',          'Physics, cosmology and general science'),
(4, 'History',          'History and social science'),
(5, 'Self-Help',        'Personal development and productivity'),
(6, 'Mathematics',      'Pure and applied mathematics');

-- PUBLISHER (5 rows)
INSERT INTO PUBLISHER (publisher_id, name, address, contact_no) VALUES
(1, 'Pearson Education',            'Knowledge Boulevard, Noida, Uttar Pradesh',  '0120-4190100'),
(2, 'McGraw-Hill Education',        'Sector 126, Noida, Uttar Pradesh',           '0120-4383600'),
(3, 'O''Reilly Media',              'Gravenstein Highway North, Sebastopol, USA', '07077899938'),
(4, 'Penguin Random House India',   'Sector 32, Gurugram, Haryana',               '0124-4786300'),
(5, 'MIT Press',                    'One Rogers Street, Cambridge, USA',          '06172531000');

-- AUTHOR (13 rows)
INSERT INTO AUTHOR (author_id, fname, minit, lname) VALUES
(1,  'Abraham', NULL, 'Silberschatz'),
(2,  'Henry',   'F',  'Korth'),
(3,  'S.',      NULL, 'Sudarshan'),
(4,  'Ramez',   NULL, 'Elmasri'),
(5,  'Shamkant','B',  'Navathe'),
(6,  'Joshua',  NULL, 'Bloch'),
(7,  'Thomas',  'H',  'Cormen'),
(8,  'George',  NULL, 'Orwell'),
(9,  'Yuval',   'N',  'Harari'),
(10, 'Stephen', 'W',  'Hawking'),
(11, 'James',   NULL, 'Clear'),
(12, 'Kenneth', 'H',  'Rosen'),
(13, 'Alan',    NULL, 'Beaulieu');

-- BOOK (13 rows)
INSERT INTO BOOK (book_id, title, isbn, category_id, publisher_id, language, edition, publication_year, price) VALUES
(1,  'Database System Concepts',               '9780078022159', 1, 2, 'English', '7th',      2019,  899.00),
(2,  'Fundamentals of Database Systems',       '9780133970777', 1, 1, 'English', '7th',      2015,  849.00),
(3,  'Effective Java',                         '9780134685991', 1, 1, 'English', '3rd',      2018,  699.00),
(4,  'Introduction to Algorithms',             '9780262046305', 1, 5, 'English', '4th',      2022, 1250.00),
(5,  'Nineteen Eighty-Four',                   '9780451524935', 2, 4, 'English', 'Reprint',  1949,  299.00),
(6,  'Animal Farm',                            '9780451526342', 2, 4, 'English', 'Reprint',  1945,  199.00),
(7,  'Sapiens: A Brief History of Humankind',  '9780062316097', 4, 4, 'English', '1st',      2015,  499.00),
(8,  'A Brief History of Time',                '9780553380163', 3, 4, 'English', '10th',     1998,  399.00),
(9,  'Atomic Habits',                          '9780735211292', 5, 4, 'English', '1st',      2018,  549.00),
(10, 'Learning SQL',                           '9780596520830', 1, 3, 'English', '2nd',      2009,  650.00),
(11, 'The Universe in a Nutshell',             '9780553802023', 3, 4, 'English', '1st',      2001,  450.00),
(12, 'Discrete Mathematics and Its Applications','9781259676512',6, 2, 'English', '8th',     2018,  799.00),
(13, 'Homo Deus: A Brief History of Tomorrow', '9780062464316', 4, 4, 'English', '1st',      2017,  549.00);

-- BOOK_AUTHOR (16 rows)
INSERT INTO BOOK_AUTHOR (book_id, author_id) VALUES
(1, 1), (1, 2), (1, 3),
(2, 4), (2, 5),
(3, 6),
(4, 7),
(5, 8),
(6, 8),
(7, 9),
(8, 10),
(9, 11),
(10, 13),
(11, 10),
(12, 12),
(13, 9);

-- STAFF_DESIGNATION (4 rows)
INSERT INTO STAFF_DESIGNATION (designation_id, designation_type, access_level) VALUES
(1, 'Library Clerk',        1),
(2, 'Assistant Librarian',  2),
(3, 'Librarian',            3),
(4, 'Branch Manager',       4);

-- LIBRARY_BRANCH (3 rows) - manager_id is filled in after STAFF is loaded
INSERT INTO LIBRARY_BRANCH (branch_id, branch_name, address, city, state, zip_code, contact_no, manager_id) VALUES
(1, 'Central Library',        'MP Nagar, Zone 1',      'Bhopal', 'Madhya Pradesh', '462001', '0755-2400101', NULL),
(2, 'North Campus Branch',    'Vijay Nagar Main Road', 'Indore', 'Madhya Pradesh', '452010', '0731-4200202', NULL),
(3, 'City Branch',            'Station Road',          'Sehore', 'Madhya Pradesh', '466001', '07562-250303', NULL);

-- STAFF (7 rows) - passwords are stored as SHA-256 hashes
INSERT INTO STAFF (staff_id, name, designation_id, branch_id, contact_no, username, password_hash) VALUES
(1, 'Ramesh Verma',  4, 1, '9876500001', 'rverma',  SHA2('staff123', 256)),
(2, 'Sunita Sharma', 4, 2, '9876500002', 'ssharma', SHA2('staff123', 256)),
(3, 'Anil Joshi',    4, 3, '9876500003', 'ajoshi',  SHA2('staff123', 256)),
(4, 'Priya Patel',   3, 1, '9876500004', 'ppatel',  SHA2('staff123', 256)),
(5, 'Rahul Mehta',   2, 2, '9876500005', 'rmehta',  SHA2('staff123', 256)),
(6, 'Neha Singh',    1, 3, '9876500006', 'nsingh',  SHA2('staff123', 256)),
(7, 'Vikram Rao',    1, 1, '9876500007', 'vrao',    SHA2('staff123', 256));

-- Assign branch managers, then add the manager foreign key
UPDATE LIBRARY_BRANCH SET manager_id = 1 WHERE branch_id = 1;
UPDATE LIBRARY_BRANCH SET manager_id = 2 WHERE branch_id = 2;
UPDATE LIBRARY_BRANCH SET manager_id = 3 WHERE branch_id = 3;

ALTER TABLE LIBRARY_BRANCH
    ADD CONSTRAINT fk_branch_manager FOREIGN KEY (manager_id) REFERENCES STAFF(staff_id);

-- BOOK_COPIES (15 rows)
-- copies_available = no_of_copies minus copies currently on loan (see BOOK_LOANS)
INSERT INTO BOOK_COPIES (copy_id, book_id, branch_id, no_of_copies, copies_available, rental_category, rental_price) VALUES
(1,  1,  1, 5, 4, 'Rentable',     20.00),
(2,  1,  2, 3, 3, 'Rentable',     20.00),
(3,  2,  1, 4, 3, 'Rentable',     20.00),
(4,  3,  2, 3, 3, 'Rentable',     15.00),
(5,  4,  1, 4, 4, 'Not_Rentable', NULL),
(6,  5,  1, 6, 5, 'Rentable',     10.00),
(7,  6,  3, 4, 4, 'Rentable',     10.00),
(8,  7,  1, 5, 4, 'Rentable',     15.00),
(9,  8,  2, 3, 2, 'Rentable',     15.00),
(10, 9,  3, 5, 4, 'Rentable',     15.00),
(11, 10, 2, 3, 2, 'Rentable',     15.00),
(12, 12, 1, 4, 4, 'Rentable',     20.00),
(13, 13, 3, 3, 3, 'Rentable',     15.00),
(14, 11, 2, 2, 2, 'Not_Rentable', NULL),
(15, 5,  3, 3, 3, 'Rentable',     10.00);

-- MEMBER (10 rows)
INSERT INTO MEMBER (card_no, fname, lname, address, phone, email, campus_id, registration_date, expiry_date, status) VALUES
(1001, 'Aarav',  'Sharma',   'Arera Colony, Bhopal',      '9811100001', 'aarav.sharma@example.com',  'CS2023001', '2025-07-01', '2027-06-30', 'Active'),
(1002, 'Diya',   'Patel',    'Vijay Nagar, Indore',       '9811100002', 'diya.patel@example.com',    'CS2023002', '2025-07-01', '2027-06-30', 'Active'),
(1003, 'Rohan',  'Gupta',    'Kolar Road, Bhopal',        '9811100003', 'rohan.gupta@example.com',   'CS2023003', '2025-07-05', '2027-07-04', 'Active'),
(1004, 'Ananya', 'Singh',    'Palasia, Indore',           '9811100004', 'ananya.singh@example.com',  'CS2023004', '2025-07-10', '2027-07-09', 'Active'),
(1005, 'Karan',  'Verma',    'Station Road, Sehore',      '9811100005', 'karan.verma@example.com',   'EC2023001', '2025-08-01', '2027-07-31', 'Active'),
(1006, 'Meera',  'Joshi',    'Shivaji Nagar, Bhopal',     '9811100006', 'meera.joshi@example.com',   'EC2023002', '2025-08-01', '2027-07-31', 'Active'),
(1007, 'Vivek',  'Yadav',    'Bhawarkua, Indore',         '9811100007', 'vivek.yadav@example.com',   NULL,        '2025-08-15', '2027-08-14', 'Active'),
(1008, 'Pooja',  'Mishra',   'Ashta Road, Sehore',        '9811100008', 'pooja.mishra@example.com',  NULL,        '2024-04-01', '2026-03-31', 'Expired'),
(1009, 'Arjun',  'Rathore',  'Habibganj, Bhopal',         '9811100009', 'arjun.rathore@example.com', 'ME2022005', '2024-07-01', '2026-12-31', 'Suspended'),
(1010, 'Sneha',  'Kulkarni', 'Rajendra Nagar, Indore',    '9811100010', 'sneha.kulkarni@example.com','CS2023010', '2025-09-01', '2027-08-31', 'Active');

-- BOOK_LOANS (14 rows)
-- Loans 1-7  : already returned (some returned late)
-- Loans 8,9,13 : not returned and overdue
-- Loans 10,11,12,14 : currently issued, not yet due (dates relative to today)
INSERT INTO BOOK_LOANS (loan_id, copy_id, card_no, issued_by_staff, date_out, due_date, date_in, lend_type, grace_period) VALUES
(1,  1,  1001, 4, '2026-07-01', '2026-07-15', '2026-07-14', 'Regular',    2),
(2,  3,  1002, 4, '2026-07-05', '2026-07-19', '2026-07-25', 'Regular',    2),
(3,  8,  1003, 7, '2026-07-10', '2026-07-24', '2026-07-22', 'Regular',    NULL),
(4,  10, 1004, 6, '2026-07-12', '2026-07-26', '2026-08-05', 'Regular',    2),
(5,  6,  1005, 4, '2026-08-01', '2026-08-15', '2026-08-12', 'Regular',    2),
(6,  9,  1006, 5, '2026-08-03', '2026-08-10', '2026-08-09', 'Short Term', 1),
(7,  12, 1010, 4, '2026-08-05', '2026-08-19', '2026-08-30', 'Regular',    NULL),
(8,  1,  1003, 4, '2026-08-20', '2026-09-03', NULL,         'Regular',    2),
(9,  6,  1004, 4, '2026-09-01', '2026-09-15', NULL,         'Regular',    2),
(10, 8,  1005, 7, DATE_SUB(CURDATE(), INTERVAL 5 DAY), DATE_ADD(CURDATE(), INTERVAL 9 DAY),  NULL, 'Regular',    2),
(11, 11, 1002, 5, DATE_SUB(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 11 DAY), NULL, 'Regular',    2),
(12, 10, 1006, 6, DATE_SUB(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 12 DAY), NULL, 'Regular',    2),
(13, 3,  1007, 4, '2026-09-10', '2026-09-24', NULL,         'Regular',    2),
(14, 9,  1001, 5, DATE_SUB(CURDATE(), INTERVAL 6 DAY), DATE_ADD(CURDATE(), INTERVAL 1 DAY),  NULL, 'Short Term', 1);

-- FINE (4 rows)
INSERT INTO FINE (fine_id, loan_id, amount, reason, paid_status, paid_date) VALUES
(1, 2, 30.00,  'Late return - 6 days',  'Paid',   '2026-07-25'),
(2, 4, 50.00,  'Late return - 10 days', 'Paid',   '2026-08-05'),
(3, 7, 55.00,  'Late return - 11 days', 'Unpaid', NULL),
(4, 5, 100.00, 'Damaged book cover',    'Unpaid', NULL);

-- RESERVATION (5 rows)
INSERT INTO RESERVATION (reservation_id, book_id, card_no, reservation_date, status, expiry_date) VALUES
(1, 13, 1002, '2026-09-25', 'Pending',   '2026-10-25'),
(2, 3,  1005, '2026-09-18', 'Fulfilled', '2026-10-02'),
(3, 4,  1008, '2026-08-30', 'Cancelled', '2026-09-13'),
(4, 9,  1003, '2026-10-01', 'Pending',   '2026-10-31'),
(5, 2,  1010, '2026-09-28', 'Pending',   '2026-10-28');

-- REVIEW (10 rows)
INSERT INTO REVIEW (review_id, book_id, card_no, rating, comment, review_date) VALUES
(1,  1,  1001, 5, 'Excellent coverage of DBMS fundamentals',          '2026-07-20'),
(2,  1,  1003, 4, 'Good book but quite heavy',                        '2026-08-02'),
(3,  2,  1002, 4, 'Very useful for ER modeling and normalization',    '2026-07-28'),
(4,  3,  1005, 5, 'Must read for Java developers',                    '2026-08-20'),
(5,  5,  1005, 5, 'A timeless classic',                               '2026-08-14'),
(6,  7,  1003, 4, 'Interesting perspective on human history',         '2026-07-25'),
(7,  9,  1004, 5, 'Simple and practical habit-building advice',       '2026-08-08'),
(8,  10, 1002, 4, 'Great introduction to SQL queries',                '2026-09-30'),
(9,  12, 1010, 3, 'Good content, dense for beginners',                '2026-09-02'),
(10, 8,  1006, 5, 'Makes cosmology accessible',                       '2026-08-12');

-- ------------------------------------------------------------
-- 4. OUTPUT / VERIFICATION
-- ------------------------------------------------------------

SHOW TABLES;

-- Row count of every table (expected: 6,5,13,13,16,4,3,7,15,10,14,4,5,10)
SELECT 'CATEGORY'          AS table_name, COUNT(*) AS total_rows FROM CATEGORY
UNION ALL SELECT 'PUBLISHER',         COUNT(*) FROM PUBLISHER
UNION ALL SELECT 'AUTHOR',            COUNT(*) FROM AUTHOR
UNION ALL SELECT 'BOOK',              COUNT(*) FROM BOOK
UNION ALL SELECT 'BOOK_AUTHOR',       COUNT(*) FROM BOOK_AUTHOR
UNION ALL SELECT 'STAFF_DESIGNATION', COUNT(*) FROM STAFF_DESIGNATION
UNION ALL SELECT 'LIBRARY_BRANCH',    COUNT(*) FROM LIBRARY_BRANCH
UNION ALL SELECT 'STAFF',             COUNT(*) FROM STAFF
UNION ALL SELECT 'BOOK_COPIES',       COUNT(*) FROM BOOK_COPIES
UNION ALL SELECT 'MEMBER',            COUNT(*) FROM MEMBER
UNION ALL SELECT 'BOOK_LOANS',        COUNT(*) FROM BOOK_LOANS
UNION ALL SELECT 'FINE',              COUNT(*) FROM FINE
UNION ALL SELECT 'RESERVATION',       COUNT(*) FROM RESERVATION
UNION ALL SELECT 'REVIEW',            COUNT(*) FROM REVIEW;

-- Contents of each table
SELECT * FROM CATEGORY;
SELECT * FROM PUBLISHER;
SELECT * FROM AUTHOR;
SELECT * FROM BOOK;
SELECT * FROM BOOK_AUTHOR;
SELECT * FROM STAFF_DESIGNATION;
SELECT * FROM LIBRARY_BRANCH;
SELECT * FROM STAFF;
SELECT * FROM BOOK_COPIES;
SELECT * FROM MEMBER;
SELECT * FROM BOOK_LOANS;
SELECT * FROM FINE;
SELECT * FROM RESERVATION;
SELECT * FROM REVIEW;