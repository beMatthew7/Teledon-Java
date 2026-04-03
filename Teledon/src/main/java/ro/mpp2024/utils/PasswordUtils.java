package ro.mpp2024.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    /**
     * The log cost factor (work factor).
     * 12 is a good balance between security and performance as of 2026.
     * Increasing this number doubles the time required to hash the password.
     */
    private static final int SALT_ROUNDS = 12;

    /**
     * Encrypts a plain-text password into a secure BCrypt hash.
     * * @param plainPassword The user's password in plain text.
     * @return A 60-character string containing the salt and the hashed password.
     */
    public static String hashPassword(String plainPassword) {
        // gensalt(SALT_ROUNDS) generates a random salt and incorporates the work factor
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(SALT_ROUNDS));
    }

    /**
     * Verifies if a plain-text password matches a previously hashed password.
     * * @param plainPassword The password entered during login.
     * @param hashed        The hashed password retrieved from the database.
     * @return true if the password is correct, false otherwise.
     */
    public static boolean verifyPassword(String plainPassword, String hashed) {
        try {
            // checkpw automatically extracts the salt from the hashed string
            return BCrypt.checkpw(plainPassword, hashed);
        } catch (Exception e) {
            // Returns false if the hash is null, empty, or not in a valid BCrypt format
            return false;
        }
    }
}
