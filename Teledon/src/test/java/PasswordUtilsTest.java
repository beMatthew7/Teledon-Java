import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ro.mpp2024.utils.PasswordUtils;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilsTest {

    @Test
    @DisplayName("Should successfully hash and verify a password")
    void testHashAndVerify() {
        String rawPassword = "parola";

        // 1. Hash the password
        String hashedPassword = PasswordUtils.hashPassword(rawPassword);
        System.out.println(hashedPassword);
        // 2. Check that the hash is not null and not empty
        assertNotNull(hashedPassword);
        assertNotEquals(rawPassword, hashedPassword);

        // 3. Verify that the correct password matches the hash
        assertTrue(PasswordUtils.verifyPassword(rawPassword, hashedPassword),
                "The password should match its own hash.");
    }

    @Test
    @DisplayName("Should fail when verifying the wrong password")
    void testWrongPassword() {
        String rawPassword = "correct_password";
        String wrongPassword = "wrong_password";

        String hashedPassword = PasswordUtils.hashPassword(rawPassword);

        // Verify that a different password does NOT match the hash
        assertFalse(PasswordUtils.verifyPassword(wrongPassword, hashedPassword),
                "A wrong password should not match the hash.");
    }

    @Test
    @DisplayName("Should generate different hashes for the same password due to random salt")
    void testRandomSalt() {
        String password = "same_password";

        String hash1 = PasswordUtils.hashPassword(password);
        String hash2 = PasswordUtils.hashPassword(password);

        // Even with the same input, hashes must be different because of the salt
        assertNotEquals(hash1, hash2, "Hashes for the same password should be unique every time.");
    }
}