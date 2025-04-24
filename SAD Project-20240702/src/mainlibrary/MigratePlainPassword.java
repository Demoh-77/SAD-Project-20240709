package mainlibrary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MigratePlainPassword {
    private static final Logger LOGGER = Logger.getLogger(MigratePlainPassword.class.getName());

    public static void main(String[] args) {
        LOGGER.info("Starting password migration...");

        try (Connection con = DB.getConnection()) { // Ensure DB.java uses env vars or is secure
            if (con == null) {
                LOGGER.severe("Failed to get database connection. Aborting migration.");
                return;
            }
            con.setAutoCommit(false); // Use transactions

            migrateUserPasswords(con);
            migrateLibrarianPasswords(con);

            con.commit(); // Commit changes if successful
            LOGGER.info("Password migration completed successfully.");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Password migration failed.", e);
            // Consider adding transaction rollback here if connection wasn't null
            System.err.println("MIGRATION FAILED! Check logs. Database changes might not be committed.");
        }
    }

    private static void migrateUserPasswords(Connection con) throws Exception {
        LOGGER.info("Migrating Users table...");
        String selectSql = "SELECT UserID, UserPass FROM Users";
        // *** CRITICAL: Check if passwords might already be hashed (look like $2a$...)
        // If so, add a WHERE clause like: WHERE UserPass NOT LIKE '$2a$%'
        // to avoid re-hashing already hashed passwords. Adapt based on BCrypt format.

        String updateSql = "UPDATE Users SET UserPass = ? WHERE UserID = ?";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(selectSql);
             PreparedStatement pstmtUpdate = con.prepareStatement(updateSql)) {

            int count = 0;
            while (rs.next()) {
                int userId = rs.getInt("UserID");
                String plainTextPassword = rs.getString("UserPass");

                 // Simple check to skip likely already hashed passwords
                if (plainTextPassword != null && plainTextPassword.startsWith("$2a$")) {
                     LOGGER.info("Skipping already hashed password for UserID: " + userId);
                     continue;
                 }


                if (plainTextPassword == null || plainTextPassword.isEmpty()) {
                    LOGGER.warning("Skipping UserID " + userId + " due to null or empty password.");
                    continue; // Skip users with no password
                }

                // Hash the plaintext password
                String hashedPassword = PasswordZone.hashedPassword(plainTextPassword);

                if (hashedPassword != null) {
                    // Update the database with the hashed password
                    pstmtUpdate.setString(1, hashedPassword);
                    pstmtUpdate.setInt(2, userId);
                    pstmtUpdate.executeUpdate();
                    count++;
                    LOGGER.info("Updated password for UserID: " + userId);
                } else {
                     LOGGER.warning("Failed to hash password for UserID: " + userId);
                }
            }
            LOGGER.info("Finished migrating " + count + " user passwords.");
        }
         // Catch specific exceptions if needed
    }

    private static void migrateLibrarianPasswords(Connection con) throws Exception {
        LOGGER.info("Migrating Librarian table...");
        String selectSql = "SELECT LibrarianID, Password FROM Librarian";
         // *** CRITICAL: Add WHERE Password NOT LIKE '$2a$%' if needed ***
        String updateSql = "UPDATE Librarian SET Password = ? WHERE LibrarianID = ?";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(selectSql);
             PreparedStatement pstmtUpdate = con.prepareStatement(updateSql)) {

            int count = 0;
            while (rs.next()) {
                int librarianId = rs.getInt("LibrarianID");
                String plainTextPassword = rs.getString("Password");

                 // Simple check to skip likely already hashed passwords
                if (plainTextPassword != null && plainTextPassword.startsWith("$2a$")) {
                     LOGGER.info("Skipping already hashed password for LibrarianID: " + librarianId);
                     continue;
                 }


                if (plainTextPassword == null || plainTextPassword.isEmpty()) {
                     LOGGER.warning("Skipping LibrarianID " + librarianId + " due to null or empty password.");
                    continue; // Skip librarians with no password
                }

                // Hash the plaintext password
                String hashedPassword = PasswordZone.hashedPassword(plainTextPassword);

                 if (hashedPassword != null) {
                    // Update the database with the hashed password
                    pstmtUpdate.setString(1, hashedPassword);
                    pstmtUpdate.setInt(2, librarianId);
                    pstmtUpdate.executeUpdate();
                    count++;
                    LOGGER.info("Updated password for LibrarianID: " + librarianId);
                 } else {
                     LOGGER.warning("Failed to hash password for LibrarianID: " + librarianId);
                 }
            }
             LOGGER.info("Finished migrating " + count + " librarian passwords.");
        }
        // Catch specific exceptions if needed
    }
}
