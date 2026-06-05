package karebes.movies.backend.modules.user.controllers;

import jakarta.validation.Valid;
import karebes.movies.backend.modules.user.dtos.requests.LoginUserRequestDTO;
import karebes.movies.backend.modules.user.dtos.requests.RefreshTokenRequestDTO;
import karebes.movies.backend.modules.user.dtos.requests.RegisterUserRequestDTO;
import karebes.movies.backend.modules.user.services.AuthService;
import karebes.movies.backend.shared.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(
            @RequestBody
            @Valid
            RegisterUserRequestDTO dataUser
    ) {

        var data = service.registerUser(dataUser);

        var res = ApiResponse.success(
                "Usuario cadastrado com sucesso",
                data
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(res);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> loginUser(
            @RequestBody
            @Valid
            LoginUserRequestDTO dataUser
    ){
        var data = service.loginUser(dataUser);

        var res = ApiResponse.success(
                "Login realizado com sucesso",
                data
        );

        return ResponseEntity.ok(res);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse> refresh(
            @RequestBody @Valid RefreshTokenRequestDTO request
    ) {
        var data = service.refreshToken(request.refreshToken());

        var res = ApiResponse.success(
                "Token atualizado com sucesso",
                data
        );

        return ResponseEntity.ok(res);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(
            @RequestBody @Valid RefreshTokenRequestDTO request
    ) {
        service.logout(request.refreshToken());

        var response = ApiResponse.success(
                "Logout realizado com sucesso",
                null
        );

        return ResponseEntity.ok(response);
    }

}
