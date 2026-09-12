package ASimulatorSystem.backend.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public final class AuthDtos {
    private AuthDtos() { }
    public record LoginRequest(@NotBlank @Pattern(regexp="\\d{12,19}") String cardNumber, @NotBlank @Pattern(regexp="\\d{4}") String pin) { }
    public record SignupRequest(@NotBlank @Pattern(regexp="\\d{12,19}") String cardNumber, @NotBlank @Pattern(regexp="\\d{4}") String pin, @NotNull @DecimalMin("0.00") @Digits(integer=13,fraction=2) BigDecimal initialDeposit) { }
    public record ChangePinRequest(@NotBlank @Pattern(regexp="\\d{4}") String currentPin, @NotBlank @Pattern(regexp="\\d{4}") String newPin) { }
    public record AuthResponse(String token, long accountId, String cardNumber) { }
}
