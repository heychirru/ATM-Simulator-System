package ASimulatorSystem.backend.controller;

import ASimulatorSystem.backend.dto.ApiDtos;
import ASimulatorSystem.backend.dto.AuthDtos;
import ASimulatorSystem.backend.entity.BankTransaction;
import ASimulatorSystem.backend.service.AtmService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/atm")
public class AtmController {
    private final AtmService atm;

    public AtmController(AtmService atm) { this.atm = atm; }

    @GetMapping("/balance")
    public ApiDtos.BalanceResponse balance(Authentication authentication) {
        return new ApiDtos.BalanceResponse(atm.balance(accountId(authentication)));
    }

    @PostMapping("/deposit")
    public ApiDtos.BalanceResponse deposit(Authentication authentication, @Valid @RequestBody ApiDtos.AmountRequest request) {
        return new ApiDtos.BalanceResponse(atm.deposit(accountId(authentication), request.amount()));
    }

    @PostMapping("/withdraw")
    public ApiDtos.BalanceResponse withdraw(Authentication authentication, @Valid @RequestBody ApiDtos.AmountRequest request) {
        return new ApiDtos.BalanceResponse(atm.withdraw(accountId(authentication), request.amount()));
    }

    @GetMapping("/transactions")
    public List<ApiDtos.TransactionResponse> transactions(Authentication authentication) {
        return atm.recent(accountId(authentication)).stream().map(this::toResponse).toList();
    }

    @PostMapping("/pin")
    public ResponseEntity<Void> changePin(Authentication authentication, @Valid @RequestBody AuthDtos.ChangePinRequest request) {
        validateDifferentPins(request.currentPin(), request.newPin());
        atm.changePin(accountId(authentication), request.currentPin(), request.newPin());
        return ResponseEntity.noContent().build();
    }

    private ApiDtos.TransactionResponse toResponse(BankTransaction t) {
        return new ApiDtos.TransactionResponse(t.getReference(), t.getType().name(), t.getAmount(), t.getBalanceAfter(), t.getCreatedAt());
    }

    private long accountId(Authentication authentication) { return ((Number) authentication.getPrincipal()).longValue(); }

    private void validateDifferentPins(String current, String next) {
        if (current.equals(next)) throw new IllegalArgumentException("New PIN must be different from the current PIN.");
    }
}
