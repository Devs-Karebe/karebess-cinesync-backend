package karebes.movies.backend.modules.user.controllers;

import jakarta.validation.Valid;
import karebes.movies.backend.modules.user.dtos.requests.ConfirmReactivateAccountRequestDTO;
import karebes.movies.backend.modules.user.dtos.requests.ForgotPasswordRequestDTO;
import karebes.movies.backend.modules.user.dtos.requests.ReactivateAccountRequestDTO;
import karebes.movies.backend.modules.user.dtos.requests.ResetPasswordRequestDTO;
import karebes.movies.backend.modules.user.services.AccountRecoveryService;
import karebes.movies.backend.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
public class AccountController {

    private final AccountRecoveryService accountRecoveryService;

    public AccountController(AccountRecoveryService accountRecoveryService) {
        this.accountRecoveryService = accountRecoveryService;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(
            @RequestBody ForgotPasswordRequestDTO request
    ) {
        String token = accountRecoveryService.forgotPassword(request.email());

        var response = ApiResponse.success(
                "Se o email existir, enviaremos instruções",
                token
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(
            @RequestBody ResetPasswordRequestDTO request
    ) {
        accountRecoveryService.resetPassword(request);

        var response = ApiResponse.success(
                "Senha redefinida com sucesso",
                null
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reactivate-account")
    public ResponseEntity<ApiResponse> requestAccountReactivation(
            @RequestBody @Valid ReactivateAccountRequestDTO request
    ) {
        String token = accountRecoveryService.requestReactivation(request.email());

        var response = ApiResponse.success(
                "Se a conta existir, instruções de reativação foram enviadas",
                token
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/confirm-reactivation")
    public ResponseEntity<ApiResponse> confirmAccountReactivation(
            @RequestBody @Valid ConfirmReactivateAccountRequestDTO request
    ) {
        accountRecoveryService.confirmReactivation(request.token(), request.password());

        var response = ApiResponse.success(
                "Conta reativada com sucesso",
                null
        );

        return ResponseEntity.ok(response);
    }
}
