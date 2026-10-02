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
            System.out.println("3. Issue Book");
            System.out.println("4. Return Book");
            System.out.println("5. View Issued Books");
            System.out.println("6. Overdue Books");
            System.out.println("7. Exit");

            int choice = readInt("\nEnter your choice: ");

            switch (choice) {
                case 1 -> bookMenu();
                case 2 -> memberMenu();
                case 3 -> issueBook();
                case 4 -> returnBook();
                case 5 -> viewIssuedBooks();
                case 6 -> viewOverdueBooks();
                case 7 -> {
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
            System.out.println("2. Search Book");
            System.out.println("3. Add Book");
            System.out.println("4. Back");

            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1 -> viewBooks();
                case 2 -> searchBook();
                case 3 -> addBook();
                case 4 -> {
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
            System.out.println("2. Add Member");
            System.out.println("3. Back");

            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1 -> viewMembers();
                case 2 -> addMember();
                case 3 -> {
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

    private static String value(String text) {
        return text == null ? "" : text;
    }

    private static void showError(SQLException e) {
        System.out.println("\nDatabase Error: " + e.getMessage());
    }
}
