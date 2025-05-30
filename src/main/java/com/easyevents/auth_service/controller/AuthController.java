package com.easyevents.auth_service.controller;

import com.easyevents.auth_service.domain.dto.request.AtualizarUsuarioRequest;
import com.easyevents.auth_service.domain.dto.request.CriarUsuarioRequest;
import com.easyevents.auth_service.domain.dto.request.LoginRequest;
import com.easyevents.auth_service.domain.dto.response.UsuarioResponse;
import com.easyevents.auth_service.domain.model.UsuarioModel;
import com.easyevents.auth_service.repository.UsuarioRepository;
import com.easyevents.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private final UsuarioRepository usuarioRepository;
    private final AuthService authService;

    @GetMapping
    public String endpointTest() {

        return "ERALDO VS MARIO!";
    }

    @GetMapping("/listar")
    public ResponseEntity<List<UsuarioModel>> listar() {

        return authService.listar();
    }

    @GetMapping("/buscar/{email}")
    public ResponseEntity<UsuarioModel> buscarPorEmail(@PathVariable String email) {
        return authService.buscarPorEmail(email);
    }

    @PostMapping("/criar")
    public ResponseEntity<UsuarioResponse> criar(@RequestBody CriarUsuarioRequest criarUsuarioRequest) {
        return authService.createUser(criarUsuarioRequest);
    }

    @PutMapping("/atualizar")
    public ResponseEntity<UsuarioResponse> atualizar(@RequestBody AtualizarUsuarioRequest atualizarUsuarioRequest) {
        return authService.updateUsuario(atualizarUsuarioRequest);
    }

    @DeleteMapping("/deletar/{email}")
    public ResponseEntity<UsuarioResponse> deletar(@PathVariable String email) {
        return authService.deleteUsuario(email);
    }

    @GetMapping("/login")
    public ResponseEntity<UsuarioResponse> login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

}
