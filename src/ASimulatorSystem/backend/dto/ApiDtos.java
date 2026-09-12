package ASimulatorSystem.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;

public final class ApiDtos {
    private ApiDtos() { }

    public record LoginRequest(
            @Pattern(regexp = "\\d{12,19}", message = "Card number must contain 12 to 19 digits") String cardNumber,
            @Pattern(regexp = "\\d{4}", message = "PIN must contain exactly 4 digits") String pin) { }

    public record LoginResponse(String token, long accountId, String cardNumber) { }

    public record SignupRequest(
            @Pattern(regexp = "\\d{12,19}", message = "Card number must contain 12 to 19 digits") String cardNumber,
            @Pattern(regexp = "\\d{4}", message = "PIN must contain exactly 4 digits") String pin,
            @DecimalMin(value = "0.00", message = "Initial deposit cannot be negative") BigDecimal initialDeposit) { }

    public record SignupResponse(long accountId, String cardNumber) { }

    public record AmountRequest(
            @DecimalMin(value = "0.01", message = "Amount must be greater than zero") BigDecimal amount) { }

    public record BalanceResponse(BigDecimal balance) { }

    public record TransactionResponse(
            String reference, String type, BigDecimal amount, BigDecimal balanceAfter, Instant createdAt) { }

    public record ErrorResponse(String error, String message, Instant timestamp) { }
}
