package karebes.movies.backend.modules.user.controllers;

import jakarta.validation.Valid;
import karebes.movies.backend.core.security.principal.UserPrincipal;
import karebes.movies.backend.modules.user.dtos.requests.UpdatePasswordRequestDTO;
import karebes.movies.backend.modules.user.dtos.requests.UpdateUserRequestDTO;
import karebes.movies.backend.modules.user.services.UserService;
import karebes.movies.backend.shared.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse> getUser(
            @AuthenticationPrincipal UserPrincipal user
    ) {

        var userId = user.getUser().getId();

        var data = service.getUser(userId);

        var res = ApiResponse.success("Usuário encontrado", data);

        return ResponseEntity.ok(res);
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse> updateUser(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody @Valid UpdateUserRequestDTO userUpdate
    ) {
        var userId = user.getUser().getId();

        var data = service.updateUser(userId, userUpdate);

        var res = ApiResponse.success("Usuário atualizado", data);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(res);
    }

    @PatchMapping("/me/password")
    public ResponseEntity<ApiResponse> changePassword(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody @Valid UpdatePasswordRequestDTO updatePassword
    ){
        var userId = user.getUser().getId();

        service.changePassword(userId, updatePassword);

        var res = ApiResponse.success("Senha alterada com sucesso", null);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(res);
    }

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse> deleteAccount(
            @AuthenticationPrincipal UserPrincipal user
    ){
        var userId = user.getUser().getId();

        service.softDeleteUser(userId);

        var res = ApiResponse.success("Conta excluída com sucesso", null);

        return ResponseEntity.ok(res);
    }
}