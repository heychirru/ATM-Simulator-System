package ASimulatorSystem.security;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class PinHasherTest {
    @Test
    void hashCanBeVerifiedWithoutStoringPlaintext() {
        String pin = "1234";
        String hash = PinHasher.hash(pin);
        assertNotEquals(pin, hash);
        assertTrue(PinHasher.matches(pin, hash));
        assertFalse(PinHasher.matches("4321", hash));
    }
}
