package com.hms;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Scanner;

public class Main {

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println("       LIBRARY MANAGEMENT SYSTEM");
        System.out.println("========================================");

        while (true) {
            System.out.println("\n1. Books");
            System.out.println("2. Members");
            System.out.println("3. Authors");
            System.out.println("4. Categories");
            System.out.println("5. Branch & Copies");
            System.out.println("6. Issue Book");
            System.out.println("7. Return Book");
            System.out.println("8. Loan Reports");
            System.out.println("9. Fines");
            System.out.println("10. Reservations");
            System.out.println("11. Reviews");
            System.out.println("12. Reports");
            System.out.println("13. Exit");

            int choice = readInt("\nEnter your choice: ");

            switch (choice) {
                case 1 -> bookMenu();
                case 2 -> memberMenu();
                case 3 -> authorMenu();
                case 4 -> categoryMenu();
                case 5 -> branchCopyMenu();
                case 6 -> issueBook();
                case 7 -> returnBook();
                case 8 -> loanReportMenu();
                case 9 -> fineMenu();
                case 10 -> reservationMenu();
                case 11 -> reviewMenu();
                case 12 -> reportsMenu();
                case 13 -> {
                    System.out.println("\nThank you for using the Library Management System.");
                    sc.close();
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // =========================================================
    // BOOK MENU
    // =========================================================

    private static void bookMenu() {

        while (true) {
            System.out.println("\n========== BOOKS ==========");
            System.out.println("1. View Books");
            System.out.println("2. Search Book by Title");
            System.out.println("3. Search Book by ISBN");
            System.out.println("4. View Available Books");
            System.out.println("5. Add Book");
            System.out.println("6. Update Book Price");
            System.out.println("7. Delete Book");
            System.out.println("8. Back");

            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1 -> viewBooks();
                case 2 -> searchBook();
                case 3 -> searchBookByISBN();
                case 4 -> viewAvailableBooks();
                case 5 -> addBook();
                case 6 -> updateBookPrice();
                case 7 -> deleteBook();
                case 8 -> {
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // =========================================================
    // VIEW BOOKS
    // =========================================================

    private static void viewBooks() {

        String sql = """
                SELECT b.book_id, b.title, b.isbn,
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
                ORDER BY b.book_id
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println();
            System.out.println("==========================================================================");
            System.out.printf("%-5s %-34s %-16s %-18s%n",
                    "ID", "TITLE", "ISBN", "CATEGORY");
            System.out.println("--------------------------------------------------------------------------");

            boolean found = false;

            while (rs.next()) {
                found = true;

                String bookTitle = value(rs.getString("title"));
                if (bookTitle.length() > 32) {
                    bookTitle = bookTitle.substring(0, 29) + "...";
                }

                String isbn = value(rs.getString("isbn"));
                String category = value(rs.getString("category_name"));

                if (isbn.length() > 15) {
                    isbn = isbn.substring(0, 15);
                }

                if (category.length() > 17) {
                    category = category.substring(0, 14) + "...";
                }

                System.out.printf("%-5d %-34s %-16s %-18s%n",
                        rs.getInt("book_id"),
                        bookTitle,
                        isbn,
                        category);
            }

            System.out.println("==========================================================================");

            if (!found) {
                System.out.println("No books found.");
            }

        } catch (SQLException e) {
            showError(e);
        }
    }

    // =========================================================
    // SEARCH BOOK
    // =========================================================

    private static void searchBook() {

        String title = readString("\nEnter book title to search: ");

        String sql = """
                SELECT b.book_id, b.title, b.isbn, c.category_name
                FROM BOOK b
                LEFT JOIN CATEGORY c
                    ON b.category_id = c.category_id
                WHERE b.title LIKE ?
                ORDER BY b.title
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + title + "%");

            try (ResultSet rs = ps.executeQuery()) {

                boolean found = false;

                System.out.println();
                System.out.println("==========================================================================");
                System.out.printf("%-5s %-34s %-16s %-18s%n",
                        "ID", "TITLE", "ISBN", "CATEGORY");
                System.out.println("--------------------------------------------------------------------------");

                while (rs.next()) {
                    found = true;

                    String bookTitle = value(rs.getString("title"));
                    if (bookTitle.length() > 32) {
                        bookTitle = bookTitle.substring(0, 29) + "...";
                    }

                    String isbn = value(rs.getString("isbn"));
                    String category = value(rs.getString("category_name"));

                    if (isbn.length() > 15) {
                        isbn = isbn.substring(0, 15);
                    }

                    if (category.length() > 17) {
                        category = category.substring(0, 14) + "...";
                    }

                    System.out.printf("%-5d %-34s %-16s %-18s%n",
                            rs.getInt("book_id"),
                            bookTitle,
                            isbn,
                            category);
                }

                System.out.println("==========================================================================");

                if (!found) {
                    System.out.println("No book found.");
                }
            }

        } catch (SQLException e) {
            showError(e);
        }
    }

    // =========================================================
    // ADD BOOK
    // =========================================================

    private static void addBook() {

        int id = readInt("\nEnter Book ID: ");
        String title = readString("Enter Title: ");
        String isbn = readString("Enter ISBN: ");
        int categoryId = readInt("Enter Category ID: ");
        int publisherId = readInt("Enter Publisher ID: ");
        String language = readString("Enter Language: ");
        String edition = readString("Enter Edition: ");
        int year = readInt("Enter Publication Year: ");
        BigDecimal price = readDecimal("Enter Price: ");

        String sql = """
                INSERT INTO BOOK
                (book_id, title, isbn, category_id, publisher_id,
                 language, edition, publication_year, price)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setString(2, title);
            ps.setString(3, isbn);
            ps.setInt(4, categoryId);
            ps.setInt(5, publisherId);
            ps.setString(6, language);
            ps.setString(7, edition);
            ps.setInt(8, year);
            ps.setBigDecimal(9, price);

            ps.executeUpdate();

            System.out.println("\nBook added successfully.");

        } catch (SQLException e) {
            showError(e);
        }
    }

    // =========================================================
    // MEMBER MENU
    // =========================================================

    private static void memberMenu() {

        while (true) {
            System.out.println("\n========= MEMBERS =========");
            System.out.println("1. View Members");
            System.out.println("2. Find Member by Card No");
            System.out.println("3. View Active Members");
            System.out.println("4. Add Member");
            System.out.println("5. Update Member Phone");
            System.out.println("6. Search Member by Name");
            System.out.println("7. Back");

            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1 -> viewMembers();
                case 2 -> findMember();
                case 3 -> viewActiveMembers();
                case 4 -> addMember();
                case 5 -> updateMemberPhone();
                case 6 -> searchMemberByName();
                case 7 -> {
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // =========================================================
    // VIEW MEMBERS
    // =========================================================

    private static void viewMembers() {

        String sql = """
                SELECT card_no, fname, lname, phone, email, status
                FROM MEMBER
                ORDER BY card_no
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println();
            System.out.println("================================================================================");
            System.out.printf("%-8s %-24s %-15s %-32s %-10s%n",
                    "CARD NO", "NAME", "PHONE", "EMAIL", "STATUS");
            System.out.println("--------------------------------------------------------------------------------");

            boolean found = false;

            while (rs.next()) {
                found = true;

                String name = value(rs.getString("fname")) + " "
                        + value(rs.getString("lname"));
                String phone = value(rs.getString("phone"));
                String email = value(rs.getString("email"));
                String status = value(rs.getString("status"));

                if (name.length() > 22) name = name.substring(0, 19) + "...";
                if (phone.length() > 14) phone = phone.substring(0, 11) + "...";
                if (email.length() > 31) email = email.substring(0, 28) + "...";
                if (status.length() > 9) status = status.substring(0, 9);

                System.out.printf("%-8d %-24s %-15s %-32s %-10s%n",
                        rs.getInt("card_no"), name, phone, email, status);
            }

            System.out.println("================================================================================");

            if (!found) {
                System.out.println("No members found.");
            }

        } catch (SQLException e) {
            showError(e);
        }
    }
    private static void addMember() {

        int cardNo = readInt("\nEnter Card No: ");
        String fname = readString("Enter First Name: ");
        String lname = readString("Enter Last Name: ");
        String address = readString("Enter Address: ");
        String phone = readString("Enter Phone: ");
        String email = readString("Enter Email: ");
        String campusId = readString("Enter Campus ID: ");

        String sql = """
                INSERT INTO MEMBER
                (card_no, fname, lname, address, phone, email,
                 campus_id, registration_date, expiry_date, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, CURDATE(),
                        DATE_ADD(CURDATE(), INTERVAL 1 YEAR), 'Active')
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, cardNo);
            ps.setString(2, fname);
            ps.setString(3, lname);
            ps.setString(4, address);
            ps.setString(5, phone);
            ps.setString(6, email);
            ps.setString(7, campusId);

            ps.executeUpdate();

            System.out.println("\nMember added successfully.");

        } catch (SQLException e) {
            showError(e);
        }
    }

    // =========================================================
    // ISSUE BOOK
    // Only Book ID + Member Card No are required.
    // =========================================================

    private static void issueBook() {

        System.out.println("\n========= ISSUE BOOK =========");

        int bookId = readInt("Enter Book ID: ");
        int cardNo = readInt("Enter Member Card No: ");

        String checkMember = """
                SELECT status
                FROM MEMBER
                WHERE card_no = ?
                """;

        String checkExistingLoan = """
                SELECT bl.loan_id
                FROM BOOK_LOANS bl
                JOIN BOOK_COPIES bc
                    ON bl.copy_id = bc.copy_id
                WHERE bc.book_id = ?
                  AND bl.card_no = ?
                  AND bl.date_in IS NULL
                LIMIT 1
                """;

        String findCopy = """
                SELECT copy_id
                FROM BOOK_COPIES
                WHERE book_id = ?
                  AND copies_available > 0
                ORDER BY copy_id
                LIMIT 1
                """;

        String findStaff = """
                SELECT staff_id
                FROM STAFF
                ORDER BY staff_id
                LIMIT 1
                """;

        String nextLoan = """
                SELECT COALESCE(MAX(loan_id), 0) + 1 AS next_id
                FROM BOOK_LOANS
                """;

        String insertLoan = """
                INSERT INTO BOOK_LOANS
                (loan_id, copy_id, card_no, issued_by_staff,
                 date_out, due_date, lend_type, grace_period)
                VALUES (?, ?, ?, ?, CURDATE(),
                        DATE_ADD(CURDATE(), INTERVAL 14 DAY),
                        'Standard', 2)
                """;

        String updateCopy = """
                UPDATE BOOK_COPIES
                SET copies_available = copies_available - 1
                WHERE copy_id = ?
                  AND copies_available > 0
                """;

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            // Check member
            try (PreparedStatement ps = con.prepareStatement(checkMember)) {

                ps.setInt(1, cardNo);

                try (ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {
                        con.rollback();
                        System.out.println("Member not found.");
                        return;
                    }

                    if (!"Active".equalsIgnoreCase(rs.getString("status"))) {
                        con.rollback();
                        System.out.println("Member is not active.");
                        return;
                    }
                }
            }

            // Prevent the same member from issuing the same book twice
            // before returning the first copy.
            try (PreparedStatement ps =
                         con.prepareStatement(checkExistingLoan)) {

                ps.setInt(1, bookId);
                ps.setInt(2, cardNo);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        con.rollback();
                        System.out.println(
                                "This member already has this book issued."
                        );
                        System.out.println(
                                "Return the book before issuing it again."
                        );
                        return;
                    }
                }
            }

            // Find an available physical copy
            int copyId;

            try (PreparedStatement ps = con.prepareStatement(findCopy)) {

                ps.setInt(1, bookId);

                try (ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {
                        con.rollback();
                        System.out.println("No available copy of this book.");
                        return;
                    }

                    copyId = rs.getInt("copy_id");
                }
            }

            // Use an existing staff member automatically
            int staffId;

            try (PreparedStatement ps = con.prepareStatement(findStaff);
                 ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    con.rollback();
                    System.out.println("No staff member found.");
                    return;
                }

                staffId = rs.getInt("staff_id");
            }

            // Generate loan ID
            int loanId;

            try (PreparedStatement ps = con.prepareStatement(nextLoan);
                 ResultSet rs = ps.executeQuery()) {

                rs.next();
                loanId = rs.getInt("next_id");
            }

            // Create loan
            try (PreparedStatement ps = con.prepareStatement(insertLoan)) {

                ps.setInt(1, loanId);
                ps.setInt(2, copyId);
                ps.setInt(3, cardNo);
                ps.setInt(4, staffId);

                ps.executeUpdate();
            }

            // Decrease available copies
            try (PreparedStatement ps = con.prepareStatement(updateCopy)) {

                ps.setInt(1, copyId);

                if (ps.executeUpdate() != 1) {
                    con.rollback();
                    System.out.println("Could not update book availability.");
                    return;
                }
            }

            con.commit();

            System.out.println();
            System.out.println("----------------------------------------");
            System.out.println("Book issued successfully.");
            System.out.println("Book ID   : " + bookId);
            System.out.println("Member No : " + cardNo);
            System.out.println("Loan ID   : " + loanId);
            System.out.println("Due Date  : " + java.time.LocalDate.now().plusDays(14));
            System.out.println("----------------------------------------");
            System.out.println("Loan ID: " + loanId);

        } catch (SQLException e) {
            showError(e);
        }
    }

    // =========================================================
    // RETURN BOOK
    // Only Book ID + Member Card No are required.
    // =========================================================

    private static void returnBook() {

        System.out.println("\n========= RETURN BOOK =========");

        int bookId = readInt("Enter Book ID: ");
        int cardNo = readInt("Enter Member Card No: ");

        String findLoan = """
                SELECT bl.loan_id, bl.copy_id
                FROM BOOK_LOANS bl
                JOIN BOOK_COPIES bc
                    ON bl.copy_id = bc.copy_id
                WHERE bc.book_id = ?
                  AND bl.card_no = ?
                  AND bl.date_in IS NULL
                ORDER BY bl.date_out
                LIMIT 1
                """;

        String updateLoan = """
                UPDATE BOOK_LOANS
                SET date_in = CURDATE()
                WHERE loan_id = ?
                  AND date_in IS NULL
                """;

        String updateCopy = """
                UPDATE BOOK_COPIES
                SET copies_available = copies_available + 1
                WHERE copy_id = ?
                """;

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            int loanId;
            int copyId;

            // Find active loan
            try (PreparedStatement ps = con.prepareStatement(findLoan)) {

                ps.setInt(1, bookId);
                ps.setInt(2, cardNo);

                try (ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {
                        con.rollback();
                        System.out.println(
                                "No active loan found for this member and book."
                        );
                        return;
                    }

                    loanId = rs.getInt("loan_id");
                    copyId = rs.getInt("copy_id");
                }
            }

            // Mark loan as returned
            try (PreparedStatement ps = con.prepareStatement(updateLoan)) {

                ps.setInt(1, loanId);

                if (ps.executeUpdate() != 1) {
                    con.rollback();
                    System.out.println("Could not return the book.");
                    return;
                }
            }

            // Increase available copies
            try (PreparedStatement ps = con.prepareStatement(updateCopy)) {

                ps.setInt(1, copyId);

                if (ps.executeUpdate() != 1) {
                    con.rollback();
                    System.out.println("Could not update book availability.");
                    return;
                }
            }

            con.commit();

            System.out.println();
            System.out.println("----------------------------------------");
            System.out.println("Book returned successfully.");
            System.out.println("Book ID   : " + bookId);
            System.out.println("Member No : " + cardNo);
            System.out.println("Loan ID   : " + loanId);
            System.out.println("----------------------------------------");

        } catch (SQLException e) {
            showError(e);
        }
    }

    // =========================================================
    // VIEW ISSUED BOOKS
    // Shows book, member, staff, issue date and due date.
    // =========================================================

    private static void viewIssuedBooks() {

        String sql = """
                SELECT b.title,
                       m.card_no,
                       CONCAT(m.fname, ' ', m.lname) AS member_name,
                       s.name AS staff_name,
                       bl.date_out,
                       bl.due_date,
                       bl.date_in
                FROM BOOK_LOANS bl
                JOIN BOOK_COPIES bc
                    ON bl.copy_id = bc.copy_id
                JOIN BOOK b
                    ON bc.book_id = b.book_id
                JOIN MEMBER m
                    ON bl.card_no = m.card_no
                JOIN STAFF s
                    ON bl.issued_by_staff = s.staff_id
                ORDER BY bl.date_out DESC
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println();
            System.out.println("==============================================================================================");
            System.out.printf("%-30s %-8s %-22s %-18s %-12s %-12s %-14s%n",
                    "BOOK", "CARD NO", "MEMBER", "ISSUED BY", "ISSUED", "DUE", "RETURNED");
            System.out.println("----------------------------------------------------------------------------------------------");

            boolean found = false;

            while (rs.next()) {
                found = true;

                String book = value(rs.getString("title"));
                String member = value(rs.getString("member_name"));
                String staff = value(rs.getString("staff_name"));
                String returned = rs.getDate("date_in") == null
                        ? "Not returned"
                        : rs.getDate("date_in").toString();

                if (book.length() > 29) book = book.substring(0, 26) + "...";
                if (member.length() > 21) member = member.substring(0, 18) + "...";
                if (staff.length() > 17) staff = staff.substring(0, 14) + "...";
                if (returned.length() > 13) returned = returned.substring(0, 13);

                System.out.printf("%-30s %-8d %-22s %-18s %-12s %-12s %-14s%n",
                        book,
                        rs.getInt("card_no"),
                        member,
                        staff,
                        rs.getDate("date_out"),
                        rs.getDate("due_date"),
                        returned);
            }

            System.out.println("==============================================================================================");

            if (!found) {
                System.out.println("No books have been issued.");
            }

        } catch (SQLException e) {
            showError(e);
        }
    }
    private static void viewOverdueBooks() {

        String sql = """
                SELECT b.title,
                       CONCAT(m.fname, ' ', m.lname) AS member_name,
                       m.card_no,
                       s.name AS staff_name,
                       bl.date_out,
                       bl.due_date
                FROM BOOK_LOANS bl
                JOIN BOOK_COPIES bc
                    ON bl.copy_id = bc.copy_id
                JOIN BOOK b
                    ON bc.book_id = b.book_id
                JOIN MEMBER m
                    ON bl.card_no = m.card_no
                JOIN STAFF s
                    ON bl.issued_by_staff = s.staff_id
                WHERE bl.date_in IS NULL
                  AND bl.due_date < CURDATE()
                ORDER BY bl.due_date
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println();
            System.out.println("============================================================================");
            System.out.printf("%-30s %-8s %-22s %-18s %-12s %-12s%n",
                    "BOOK", "CARD NO", "MEMBER", "ISSUED BY", "ISSUED", "DUE");
            System.out.println("----------------------------------------------------------------------------");

            boolean found = false;

            while (rs.next()) {
                found = true;

                String book = value(rs.getString("title"));
                String member = value(rs.getString("member_name"));
                String staff = value(rs.getString("staff_name"));

                if (book.length() > 29) book = book.substring(0, 26) + "...";
                if (member.length() > 21) member = member.substring(0, 18) + "...";
                if (staff.length() > 17) staff = staff.substring(0, 14) + "...";

                System.out.printf("%-30s %-8d %-22s %-18s %-12s %-12s%n",
                        book,
                        rs.getInt("card_no"),
                        member,
                        staff,
                        rs.getDate("date_out"),
                        rs.getDate("due_date"));
            }

            System.out.println("============================================================================");

            if (!found) {
                System.out.println("No overdue books.");
            }

        } catch (SQLException e) {
            showError(e);
        }
    }
    private static void authorMenu() {
        while (true) {
            System.out.println("\n========== AUTHORS ==========");
            System.out.println("1. View Authors");
            System.out.println("2. View Books with Authors");
            System.out.println("3. Back");
            int choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1 -> viewAuthors();
                case 2 -> viewBooksWithAuthors();
                case 3 -> { return; }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void viewAuthors() {
        String sql = "SELECT author_id, fname, minit, lname FROM AUTHOR ORDER BY lname, fname";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("\n===============================================================");
            System.out.printf("%-10s %-18s %-8s %-18s%n", "ID", "FIRST NAME", "M.I.", "LAST NAME");
            System.out.println("---------------------------------------------------------------");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-10d %-18s %-8s %-18s%n", rs.getInt("author_id"), value(rs.getString("fname")), value(rs.getString("minit")), value(rs.getString("lname")));
            }
            System.out.println("===============================================================");
            if (!found) System.out.println("No authors found.");
        } catch (SQLException e) { showError(e); }
    }

    private static void viewBooksWithAuthors() {
        String sql = """
                SELECT b.book_id, b.title,
                       CONCAT(a.fname, ' ', IF(a.minit IS NULL OR a.minit = '', '', CONCAT(a.minit, '. ')), a.lname) AS author_name
                FROM BOOK b
                JOIN BOOK_AUTHOR ba ON b.book_id = ba.book_id
                JOIN AUTHOR a ON ba.author_id = a.author_id
                ORDER BY b.title, author_name
                """;
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            System.out.println("\n================================================================================");
            System.out.printf("%-8s %-40s %-25s%n", "ID", "BOOK", "AUTHOR");
            System.out.println("--------------------------------------------------------------------------------");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-8d %-40s %-25s%n", rs.getInt("book_id"), trim(value(rs.getString("title")), 39), trim(value(rs.getString("author_name")), 24));
            }
            System.out.println("================================================================================");
            if (!found) System.out.println("No book-author relationships found.");
        } catch (SQLException e) { showError(e); }
    }

    private static void categoryMenu() {
        while (true) {
            System.out.println("\n========= CATEGORIES =========");
            System.out.println("1. View Categories");
            System.out.println("2. Count Books by Category");
            System.out.println("3. Back");
            int choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1 -> viewCategories();
                case 2 -> countBooksByCategory();
                case 3 -> { return; }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void viewCategories() {
        String sql = "SELECT category_id, category_name, description FROM CATEGORY ORDER BY category_name";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            System.out.println("\n================================================================================");
            System.out.printf("%-10s %-25s %-40s%n", "ID", "CATEGORY", "DESCRIPTION");
            System.out.println("--------------------------------------------------------------------------------");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-10d %-25s %-40s%n", rs.getInt("category_id"), trim(value(rs.getString("category_name")), 24), trim(value(rs.getString("description")), 39));
            }
            System.out.println("================================================================================");
            if (!found) System.out.println("No categories found.");
        } catch (SQLException e) { showError(e); }
    }

    private static void countBooksByCategory() {
        String sql = """
                SELECT c.category_name, COUNT(b.book_id) AS total_books
                FROM CATEGORY c LEFT JOIN BOOK b ON c.category_id = b.category_id
                GROUP BY c.category_id, c.category_name ORDER BY c.category_name
                """;
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            System.out.println("\n=====================================");
            System.out.printf("%-25s %-10s%n", "CATEGORY", "BOOKS");
            System.out.println("-------------------------------------");
            boolean found = false;
            while (rs.next()) { found = true; System.out.printf("%-25s %-10d%n", trim(value(rs.getString("category_name")), 24), rs.getInt("total_books")); }
            System.out.println("=====================================");
            if (!found) System.out.println("No category data found.");
        } catch (SQLException e) { showError(e); }
    }

    private static void branchCopyMenu() {
        while (true) {
            System.out.println("\n======= BRANCH & COPIES =======");
            System.out.println("1. View Copies by Branch");
            System.out.println("2. Find Book Across Branches");
            System.out.println("3. Back");
            int choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1 -> viewCopiesByBranch();
                case 2 -> findBookAcrossBranches();
                case 3 -> { return; }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void viewCopiesByBranch() {
        String sql = """
                SELECT bc.copy_id, b.title, lb.branch_name, bc.no_of_copies, bc.copies_available, bc.rental_category, bc.rental_price
                FROM BOOK_COPIES bc
                JOIN BOOK b ON bc.book_id = b.book_id
                JOIN LIBRARY_BRANCH lb ON bc.branch_id = lb.branch_id
                ORDER BY lb.branch_name, b.title
                """;
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            System.out.println("\n===============================================================================================");
            System.out.printf("%-8s %-30s %-22s %-8s %-10s %-16s %-10s%n", "COPY", "BOOK", "BRANCH", "TOTAL", "AVAILABLE", "RENTAL TYPE", "PRICE");
            System.out.println("-----------------------------------------------------------------------------------------------");
            boolean found = false;
            while (rs.next()) {
                found = true;
                BigDecimal rentalPrice = rs.getBigDecimal("rental_price");
                System.out.printf("%-8d %-30s %-22s %-8d %-10d %-16s %-10s%n", rs.getInt("copy_id"), trim(value(rs.getString("title")), 29), trim(value(rs.getString("branch_name")), 21), rs.getInt("no_of_copies"), rs.getInt("copies_available"), trim(value(rs.getString("rental_category")), 15), rentalPrice == null ? "-" : rentalPrice);
            }
            System.out.println("===============================================================================================");
            if (!found) System.out.println("No book copies found.");
        } catch (SQLException e) { showError(e); }
    }

    private static void findBookAcrossBranches() {
        String keyword = readString("\nEnter book title/keyword: ");
        String sql = """
                SELECT b.title, lb.branch_name, bc.copies_available
                FROM BOOK b JOIN BOOK_COPIES bc ON b.book_id = bc.book_id
                JOIN LIBRARY_BRANCH lb ON bc.branch_id = lb.branch_id
                WHERE b.title LIKE ?
                ORDER BY lb.branch_name, b.title
                """;
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("\n============================================================");
                System.out.printf("%-35s %-22s %-10s%n", "BOOK", "BRANCH", "AVAILABLE");
                System.out.println("------------------------------------------------------------");
                boolean found = false;
                while (rs.next()) { found = true; System.out.printf("%-35s %-22s %-10d%n", trim(value(rs.getString("title")), 34), trim(value(rs.getString("branch_name")), 21), rs.getInt("copies_available")); }
                System.out.println("============================================================");
                if (!found) System.out.println("No matching books found.");
            }
        } catch (SQLException e) { showError(e); }
    }

    private static void loanReportMenu() {
        while (true) {
            System.out.println("\n========== LOAN REPORTS ==========");
            System.out.println("1. Currently Issued Books");
            System.out.println("2. Member Borrowing History");
            System.out.println("3. Overdue Books");
            System.out.println("4. Back");
            int choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1 -> viewIssuedBooks();
                case 2 -> viewMemberBorrowingHistory();
                case 3 -> viewOverdueBooks();
                case 4 -> { return; }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void viewMemberBorrowingHistory() {
        int cardNo = readInt("\nEnter Member Card No: ");
        String sql = """
                SELECT bl.loan_id, b.title, bl.date_out, bl.due_date, bl.date_in, bl.lend_type
                FROM BOOK_LOANS bl
                JOIN BOOK_COPIES bc ON bl.copy_id = bc.copy_id
                JOIN BOOK b ON bc.book_id = b.book_id
                WHERE bl.card_no = ?
                ORDER BY bl.date_out DESC
                """;
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cardNo);
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("\n================================================================================");
                System.out.printf("%-8s %-36s %-12s %-12s %-12s %-15s%n", "LOAN", "BOOK", "OUT", "DUE", "RETURNED", "TYPE");
                System.out.println("--------------------------------------------------------------------------------");
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    Date returned = rs.getDate("date_in");
                    System.out.printf("%-8d %-36s %-12s %-12s %-12s %-15s%n", rs.getInt("loan_id"), trim(value(rs.getString("title")), 35), rs.getDate("date_out"), rs.getDate("due_date"), returned == null ? "-" : returned, trim(value(rs.getString("lend_type")), 14));
                }
                System.out.println("================================================================================");
                if (!found) System.out.println("No borrowing history found.");
            }
        } catch (SQLException e) { showError(e); }
    }

    private static void fineMenu() {
        while (true) {
            System.out.println("\n============ FINES ============");
            System.out.println("1. View Unpaid Fines");
            System.out.println("2. Add Fine");
            System.out.println("3. Mark Fine as Paid");
            System.out.println("4. Back");
            int choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1 -> viewUnpaidFines();
                case 2 -> addFine();
                case 3 -> markFinePaid();
                case 4 -> { return; }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void viewUnpaidFines() {
        String sql = """
                SELECT f.fine_id, f.loan_id, m.card_no, CONCAT(m.fname, ' ', m.lname) AS member_name,
                       b.title, f.amount, f.reason, f.paid_status
                FROM FINE f JOIN BOOK_LOANS bl ON f.loan_id = bl.loan_id
                JOIN MEMBER m ON bl.card_no = m.card_no
                JOIN BOOK_COPIES bc ON bl.copy_id = bc.copy_id
                JOIN BOOK b ON bc.book_id = b.book_id
                WHERE f.paid_status = 'Unpaid'
                ORDER BY f.fine_id
                """;
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            System.out.println("\n================================================================================================");
            System.out.printf("%-7s %-7s %-9s %-20s %-30s %-10s %-20s%n", "FINE", "LOAN", "CARD", "MEMBER", "BOOK", "AMOUNT", "REASON");
            System.out.println("------------------------------------------------------------------------------------------------");
            boolean found = false;
            while (rs.next()) { found = true; System.out.printf("%-7d %-7d %-9d %-20s %-30s %-10s %-20s%n", rs.getInt("fine_id"), rs.getInt("loan_id"), rs.getInt("card_no"), trim(value(rs.getString("member_name")), 19), trim(value(rs.getString("title")), 29), rs.getBigDecimal("amount"), trim(value(rs.getString("reason")), 19)); }
            System.out.println("================================================================================================");
            if (!found) System.out.println("No unpaid fines.");
        } catch (SQLException e) { showError(e); }
    }

    private static void addFine() {
        int fineId = readInt("\nEnter Fine ID: ");
        int loanId = readInt("Enter Loan ID: ");
        BigDecimal amount = readDecimal("Enter Fine Amount: ");
        String reason = readString("Enter Reason: ");
        String sql = "INSERT INTO FINE (fine_id, loan_id, amount, reason, paid_status) VALUES (?, ?, ?, ?, 'Unpaid')";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, fineId); ps.setInt(2, loanId); ps.setBigDecimal(3, amount); ps.setString(4, reason);
            ps.executeUpdate(); System.out.println("Fine added successfully.");
        } catch (SQLException e) { showError(e); }
    }

    private static void markFinePaid() {
        int fineId = readInt("\nEnter Fine ID: ");
        String sql = "UPDATE FINE SET paid_status = 'Paid', paid_date = CURDATE() WHERE fine_id = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, fineId); System.out.println(ps.executeUpdate() == 1 ? "Fine marked as paid." : "Fine not found.");
        } catch (SQLException e) { showError(e); }
    }

    private static void reservationMenu() {
        while (true) {
            System.out.println("\n======== RESERVATIONS ========");
            System.out.println("1. View Pending Reservations");
            System.out.println("2. Add Reservation");
            System.out.println("3. Cancel Reservation");
            System.out.println("4. Back");
            int choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1 -> viewPendingReservations();
                case 2 -> addReservation();
                case 3 -> cancelReservation();
                case 4 -> { return; }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void viewPendingReservations() {
        String sql = """
                SELECT r.reservation_id, b.title, m.card_no,
                       CONCAT(m.fname, ' ', m.lname) AS member_name,
                       r.reservation_date, r.status, r.expiry_date
                FROM RESERVATION r JOIN BOOK b ON r.book_id = b.book_id
                JOIN MEMBER m ON r.card_no = m.card_no
                WHERE r.status = 'Pending'
                ORDER BY r.reservation_date
                """;
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            System.out.println("\n================================================================================================");
            System.out.printf("%-8s %-30s %-8s %-22s %-14s %-12s %-14s%n", "RES", "BOOK", "CARD", "MEMBER", "DATE", "STATUS", "EXPIRY");
            System.out.println("------------------------------------------------------------------------------------------------");
            boolean found = false;
            while (rs.next()) { found = true; System.out.printf("%-8d %-30s %-8d %-22s %-14s %-12s %-14s%n", rs.getInt("reservation_id"), trim(value(rs.getString("title")), 29), rs.getInt("card_no"), trim(value(rs.getString("member_name")), 21), rs.getDate("reservation_date"), value(rs.getString("status")), rs.getDate("expiry_date")); }
            System.out.println("================================================================================================");
            if (!found) System.out.println("No pending reservations.");
        } catch (SQLException e) { showError(e); }
    }

    private static void addReservation() {
        int reservationId = readInt("\nEnter Reservation ID: ");
        int bookId = readInt("Enter Book ID: ");
        int cardNo = readInt("Enter Member Card No: ");
        String sql = """
                INSERT INTO RESERVATION (reservation_id, book_id, card_no, reservation_date, status, expiry_date)
                VALUES (?, ?, ?, CURDATE(), 'Pending', DATE_ADD(CURDATE(), INTERVAL 3 DAY))
                """;
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, reservationId); ps.setInt(2, bookId); ps.setInt(3, cardNo); ps.executeUpdate();
            System.out.println("Reservation added successfully.");
        } catch (SQLException e) { showError(e); }
    }

    private static void cancelReservation() {
        int reservationId = readInt("\nEnter Reservation ID: ");
        String sql = "UPDATE RESERVATION SET status = 'Cancelled' WHERE reservation_id = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, reservationId); System.out.println(ps.executeUpdate() == 1 ? "Reservation cancelled." : "Reservation not found.");
        } catch (SQLException e) { showError(e); }
    }

    private static void reviewMenu() {
        while (true) {
            System.out.println("\n=========== REVIEWS ===========");
            System.out.println("1. View Reviews for a Book");
            System.out.println("2. Add Review");
            System.out.println("3. Average Rating for Each Book");
            System.out.println("4. Back");
            int choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1 -> viewReviewsForBook();
                case 2 -> addReview();
                case 3 -> averageRatings();
                case 4 -> { return; }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void viewReviewsForBook() {
        int bookId = readInt("\nEnter Book ID: ");
        String sql = """
                SELECT r.review_id, b.title, m.card_no, CONCAT(m.fname, ' ', m.lname) AS member_name,
                       r.rating, r.comment, r.review_date
                FROM REVIEW r JOIN BOOK b ON r.book_id = b.book_id
                JOIN MEMBER m ON r.card_no = m.card_no
                WHERE b.book_id = ?
                ORDER BY r.review_date DESC
                """;
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("\n================================================================================================");
                System.out.printf("%-7s %-30s %-8s %-20s %-8s %-30s %-12s%n", "REVIEW", "BOOK", "CARD", "MEMBER", "RATING", "COMMENT", "DATE");
                System.out.println("------------------------------------------------------------------------------------------------");
                boolean found = false;
                while (rs.next()) { found = true; System.out.printf("%-7d %-30s %-8d %-20s %-8d %-30s %-12s%n", rs.getInt("review_id"), trim(value(rs.getString("title")), 29), rs.getInt("card_no"), trim(value(rs.getString("member_name")), 19), rs.getInt("rating"), trim(value(rs.getString("comment")), 29), rs.getDate("review_date")); }
                System.out.println("================================================================================================");
                if (!found) System.out.println("No reviews found for this book.");
            }
        } catch (SQLException e) { showError(e); }
    }

    private static void addReview() {
        int reviewId = readInt("\nEnter Review ID: ");
        int bookId = readInt("Enter Book ID: ");
        int cardNo = readInt("Enter Member Card No: ");
        int rating;
        do {
            rating = readInt("Enter Rating (1-5): ");
            if (rating < 1 || rating > 5) System.out.println("Rating must be between 1 and 5.");
        } while (rating < 1 || rating > 5);
        String comment = readString("Enter Comment: ");
        String sql = "INSERT INTO REVIEW (review_id, book_id, card_no, rating, comment, review_date) VALUES (?, ?, ?, ?, ?, CURDATE())";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, reviewId); ps.setInt(2, bookId); ps.setInt(3, cardNo); ps.setInt(4, rating); ps.setString(5, comment); ps.executeUpdate();
            System.out.println("Review added successfully.");
        } catch (SQLException e) { showError(e); }
    }

    private static void averageRatings() {
        String sql = """
                SELECT b.book_id, b.title, ROUND(AVG(r.rating), 2) AS average_rating, COUNT(r.review_id) AS total_reviews
                FROM BOOK b LEFT JOIN REVIEW r ON b.book_id = r.book_id
                GROUP BY b.book_id, b.title
                ORDER BY average_rating DESC
                """;
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            System.out.println("\n===============================================================");
            System.out.printf("%-8s %-40s %-12s %-12s%n", "ID", "BOOK", "AVG RATING", "REVIEWS");
            System.out.println("---------------------------------------------------------------");
            boolean found = false;
            while (rs.next()) { found = true; Object avg = rs.getObject("average_rating"); System.out.printf("%-8d %-40s %-12s %-12d%n", rs.getInt("book_id"), trim(value(rs.getString("title")), 39), avg == null ? "-" : avg.toString(), rs.getInt("total_reviews")); }
            System.out.println("===============================================================");
            if (!found) System.out.println("No books found.");
        } catch (SQLException e) { showError(e); }
    }

    private static void reportsMenu() {
        while (true) {
            System.out.println("\n============ REPORTS ============");
            System.out.println("1. Books by Category");
            System.out.println("2. Most Borrowed Books");
            System.out.println("3. Total Books");
            System.out.println("4. Total Available Copies");
            System.out.println("5. Members with Active Loans");
            System.out.println("6. Back");
            int choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1 -> countBooksByCategory();
                case 2 -> mostBorrowedBooks();
                case 3 -> totalBooks();
                case 4 -> totalAvailableCopies();
                case 5 -> membersWithActiveLoans();
                case 6 -> { return; }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void mostBorrowedBooks() {
        String sql = """
                SELECT b.book_id, b.title, COUNT(bl.loan_id) AS times_borrowed
                FROM BOOK_LOANS bl JOIN BOOK_COPIES bc ON bl.copy_id = bc.copy_id
                JOIN BOOK b ON bc.book_id = b.book_id
                GROUP BY b.book_id, b.title
                ORDER BY times_borrowed DESC
                """;
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            System.out.println("\n==============================================================");
            System.out.printf("%-8s %-42s %-14s%n", "ID", "BOOK", "BORROWED");
            System.out.println("--------------------------------------------------------------");
            boolean found = false;
            while (rs.next()) { found = true; System.out.printf("%-8d %-42s %-14d%n", rs.getInt("book_id"), trim(value(rs.getString("title")), 41), rs.getInt("times_borrowed")); }
            System.out.println("==============================================================");
            if (!found) System.out.println("No loan data found.");
        } catch (SQLException e) { showError(e); }
    }

    private static void totalBooks() {
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) AS total_books FROM BOOK"); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) System.out.println("\nTotal number of books: " + rs.getInt("total_books"));
        } catch (SQLException e) { showError(e); }
    }

    private static void totalAvailableCopies() {
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement("SELECT SUM(copies_available) AS total_available_copies FROM BOOK_COPIES"); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) System.out.println("\nTotal available copies: " + rs.getObject("total_available_copies"));
        } catch (SQLException e) { showError(e); }
    }

    private static void membersWithActiveLoans() {
        String sql = """
                SELECT m.card_no, CONCAT(m.fname, ' ', m.lname) AS member_name, COUNT(bl.loan_id) AS active_loans
                FROM MEMBER m JOIN BOOK_LOANS bl ON m.card_no = bl.card_no
                WHERE bl.date_in IS NULL
                GROUP BY m.card_no, m.fname, m.lname
                ORDER BY active_loans DESC
                """;
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            System.out.println("\n====================================================");
            System.out.printf("%-10s %-25s %-12s%n", "CARD NO", "MEMBER", "ACTIVE LOANS");
            System.out.println("----------------------------------------------------");
            boolean found = false;
            while (rs.next()) { found = true; System.out.printf("%-10d %-25s %-12d%n", rs.getInt("card_no"), trim(value(rs.getString("member_name")), 24), rs.getInt("active_loans")); }
            System.out.println("====================================================");
            if (!found) System.out.println("No active loans found.");
        } catch (SQLException e) { showError(e); }
    }

    private static void searchBookByISBN() {
        String isbn = readString("\nEnter ISBN: ");
        String sql = "SELECT * FROM BOOK WHERE isbn = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, isbn);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\nBook found:");
                    System.out.println("Book ID          : " + rs.getInt("book_id"));
                    System.out.println("Title            : " + value(rs.getString("title")));
                    System.out.println("ISBN             : " + value(rs.getString("isbn")));
                    System.out.println("Category ID      : " + rs.getInt("category_id"));
                    System.out.println("Publisher ID     : " + rs.getInt("publisher_id"));
                    System.out.println("Language         : " + value(rs.getString("language")));
                    System.out.println("Edition          : " + value(rs.getString("edition")));
                    System.out.println("Publication Year : " + rs.getInt("publication_year"));
                    System.out.println("Price            : " + rs.getBigDecimal("price"));
                } else System.out.println("Book not found.");
            }
        } catch (SQLException e) { showError(e); }
    }

    private static void viewAvailableBooks() {
        String sql = "SELECT b.book_id, b.title, bc.branch_id, bc.copies_available FROM BOOK b JOIN BOOK_COPIES bc ON b.book_id = bc.book_id WHERE bc.copies_available > 0 ORDER BY b.title";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            System.out.println("\n===============================================================");
            System.out.printf("%-8s %-40s %-12s %-12s%n", "ID", "BOOK", "BRANCH", "AVAILABLE");
            System.out.println("---------------------------------------------------------------");
            boolean found = false;
            while (rs.next()) { found = true; System.out.printf("%-8d %-40s %-12d %-12d%n", rs.getInt("book_id"), trim(value(rs.getString("title")), 39), rs.getInt("branch_id"), rs.getInt("copies_available")); }
            System.out.println("===============================================================");
            if (!found) System.out.println("No available books found.");
        } catch (SQLException e) { showError(e); }
    }

    private static void updateBookPrice() {
        int bookId = readInt("\nEnter Book ID: ");
        BigDecimal price = readDecimal("Enter New Price: ");
        String sql = "UPDATE BOOK SET price = ? WHERE book_id = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBigDecimal(1, price); ps.setInt(2, bookId);
            System.out.println(ps.executeUpdate() == 1 ? "Book price updated successfully." : "Book not found.");
        } catch (SQLException e) { showError(e); }
    }

    private static void deleteBook() {
        int bookId = readInt("\nEnter Book ID to delete: ");
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement("DELETE FROM BOOK WHERE book_id = ?")) {
            ps.setInt(1, bookId);
            System.out.println(ps.executeUpdate() == 1 ? "Book deleted successfully." : "Book not found.");
        } catch (SQLException e) { showError(e); }
    }

    private static void findMember() {
        int cardNo = readInt("\nEnter Member Card No: ");
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement("SELECT * FROM MEMBER WHERE card_no = ?")) {
            ps.setInt(1, cardNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\nMember found:");
                    System.out.println("Card No           : " + rs.getInt("card_no"));
                    System.out.println("Name              : " + value(rs.getString("fname")) + " " + value(rs.getString("lname")));
                    System.out.println("Address           : " + value(rs.getString("address")));
                    System.out.println("Phone             : " + value(rs.getString("phone")));
                    System.out.println("Email             : " + value(rs.getString("email")));
                    System.out.println("Campus ID         : " + value(rs.getString("campus_id")));
                    System.out.println("Registration Date : " + rs.getDate("registration_date"));
                    System.out.println("Expiry Date       : " + rs.getDate("expiry_date"));
                    System.out.println("Status            : " + value(rs.getString("status")));
                } else System.out.println("Member not found.");
            }
        } catch (SQLException e) { showError(e); }
    }

    private static void viewActiveMembers() {
        String sql = "SELECT card_no, CONCAT(fname, ' ', lname) AS member_name, phone, email, expiry_date FROM MEMBER WHERE status = 'Active' ORDER BY lname, fname";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            System.out.println("\n=============================================================================");
            System.out.printf("%-10s %-25s %-15s %-30s %-12s%n", "CARD NO", "NAME", "PHONE", "EMAIL", "EXPIRY");
            System.out.println("-----------------------------------------------------------------------------");
            boolean found = false;
            while (rs.next()) { found = true; System.out.printf("%-10d %-25s %-15s %-30s %-12s%n", rs.getInt("card_no"), trim(value(rs.getString("member_name")), 24), value(rs.getString("phone")), trim(value(rs.getString("email")), 29), rs.getDate("expiry_date")); }
            System.out.println("=============================================================================");
            if (!found) System.out.println("No active members found.");
        } catch (SQLException e) { showError(e); }
    }

    private static void updateMemberPhone() {
        int cardNo = readInt("\nEnter Member Card No: ");
        String phone = readString("Enter New Phone: ");
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement("UPDATE MEMBER SET phone = ? WHERE card_no = ?")) {
            ps.setString(1, phone); ps.setInt(2, cardNo);
            System.out.println(ps.executeUpdate() == 1 ? "Member phone updated successfully." : "Member not found.");
        } catch (SQLException e) { showError(e); }
    }

    private static void searchMemberByName() {
        String name = readString("\nEnter first or last name: ");
        String sql = "SELECT card_no, CONCAT(fname, ' ', lname) AS member_name, phone, email, status FROM MEMBER WHERE fname LIKE ? OR lname LIKE ? ORDER BY lname, fname";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + name + "%"); ps.setString(2, "%" + name + "%");
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("\n================================================================================");
                System.out.printf("%-10s %-25s %-15s %-30s %-10s%n", "CARD NO", "NAME", "PHONE", "EMAIL", "STATUS");
                System.out.println("--------------------------------------------------------------------------------");
                boolean found = false;
                while (rs.next()) { found = true; System.out.printf("%-10d %-25s %-15s %-30s %-10s%n", rs.getInt("card_no"), trim(value(rs.getString("member_name")), 24), trim(value(rs.getString("phone")), 14), trim(value(rs.getString("email")), 29), trim(value(rs.getString("status")), 9)); }
                System.out.println("================================================================================");
                if (!found) System.out.println("No matching member found.");
            }
        } catch (SQLException e) { showError(e); }
    }

    private static int readInt(String message) {

        while (true) {
            try {
                System.out.print(message);
                return Integer.parseInt(sc.nextLine().trim());

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static BigDecimal readDecimal(String message) {

        while (true) {
            try {
                System.out.print(message);
                return new BigDecimal(sc.nextLine().trim());

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid price.");
            }
        }
    }

    private static String readString(String message) {
        System.out.print(message);
        return sc.nextLine().trim();
    }

    private static String trim(String text, int max) {
        if (text == null) return "";
        return text.length() <= max ? text : text.substring(0, Math.max(0, max - 3)) + "...";
    }

    private static String value(String text) {
        return text == null ? "" : text;
    }

    private static void showError(SQLException e) {
        System.out.println("\nDatabase Error: " + e.getMessage());
    }
}
