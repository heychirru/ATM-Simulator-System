package ASimulatorSystem.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Hashes ATM PINs; plaintext PINs are never stored. */
public final class PinHasher {
    private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder(12);
    private PinHasher() { }
    public static String hash(String pin) { return ENCODER.encode(pin); }
    public static boolean matches(String pin, String hash) { return ENCODER.matches(pin, hash); }
}
