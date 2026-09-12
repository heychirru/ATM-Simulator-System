package ASimulatorSystem.backend.controller;

import ASimulatorSystem.backend.dto.AuthDtos;
import ASimulatorSystem.backend.entity.Account;
import ASimulatorSystem.backend.security.JwtService;
import ASimulatorSystem.backend.service.AtmService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AtmService atm;
    private final JwtService jwt;

    public AuthController(AtmService atm, JwtService jwt) {
        this.atm = atm;
        this.jwt = jwt;
    }

    @PostMapping("/login")
    public AuthDtos.AuthResponse login(@Valid @RequestBody AuthDtos.LoginRequest request) {
        Account account = atm.authenticate(request.cardNumber(), request.pin());
        return new AuthDtos.AuthResponse(jwt.generate(account.getId(), account.getCardNumber()), account.getId(), account.getCardNumber());
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthDtos.AuthResponse signup(@Valid @RequestBody AuthDtos.SignupRequest request) {
        Account account = atm.createAccount(request.cardNumber(), request.pin(), request.initialDeposit());
        return new AuthDtos.AuthResponse(jwt.generate(account.getId(), account.getCardNumber()), account.getId(), account.getCardNumber());
    }
}
