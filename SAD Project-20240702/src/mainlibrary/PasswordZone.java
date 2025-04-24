package mainlibrary; // Corrected package name
import org.mindrot.jbcrypt.BCrypt; // Import BCrypt
 
public class PasswordZone {
 
    // Method to hash a password

    public static String hashedPassword(String plainZonePassword) {
        if (plainZonePassword == null || plainZonePassword.isEmpty()) {
            // Handle empty password appropriately, maybe throw exception
            return null;
        }
        // Hash the password with a randomly generated salt
        // The salt is included in the generated hash string
        return BCrypt.hashpw(plainZonePassword, BCrypt.gensalt());
    }
 
    // Method to check a password against a stored hash
    public static boolean checkPassword(String plainZonePassword, String hashedPassword) {
         if (plainZonePassword == null || hashedPassword == null || hashedPassword.isEmpty()) {
            return false;
        }
        try {
           return BCrypt.checkpw(plainZonePassword, hashedPassword);
        } catch (IllegalArgumentException e) {

           // Handles cases where the hash format is invalid

           System.err.println("Error checking password: Invalid hash format. " + e.getMessage());

           return false;

        }

    }

}