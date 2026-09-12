package ASimulatorSystem.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.Instant;

public final class ApiDtos {
    private ApiDtos() { }
    public record LoginRequest(
            @NotBlank @Pattern(regexp = "\\d{12,19}", message = "Card number must contain 12 to 19 digits") String cardNumber,
            @NotBlank @Pattern(regexp = "\\d{4}", message = "PIN must contain exactly 4 digits") String pin) { }
    public record LoginResponse(String token, long accountId, String cardNumber) { }
    public record SignupRequest(
            @NotBlank @Pattern(regexp = "\\d{12,19}", message = "Card number must contain 12 to 19 digits") String cardNumber,
            @NotBlank @Pattern(regexp = "\\d{4}", message = "PIN must contain exactly 4 digits") String pin,
            @NotNull @DecimalMin(value = "0.00", message = "Initial deposit cannot be negative") BigDecimal initialDeposit) { }
    public record SignupResponse(long accountId, String cardNumber) { }
    public record AmountRequest(@NotNull @DecimalMin(value = "0.01", message = "Amount must be greater than zero") BigDecimal amount) { }
    public record BalanceResponse(BigDecimal balance) { }
    public record TransactionResponse(String reference, String type, BigDecimal amount, BigDecimal balanceAfter, Instant createdAt) { }
    public record ErrorResponse(String error, String message, Instant timestamp) { }
}
