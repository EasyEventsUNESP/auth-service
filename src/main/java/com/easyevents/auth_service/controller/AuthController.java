package com.easyevents.auth_service.controller;

import com.easyevents.auth_service.domain.dto.request.AtualizarUsuarioRequest;
import com.easyevents.auth_service.domain.dto.request.CriarUsuarioRequest;
import com.easyevents.auth_service.domain.dto.response.UsuarioResponse;
import com.easyevents.auth_service.domain.model.Usuario;
import com.easyevents.auth_service.repository.UsuarioRepository;
import com.easyevents.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    private final AuthService authService;

    @GetMapping
    public String endpointTest() {

        return "ERALDO VS MARIO!";
    }

    @GetMapping("/listar")
    public List<Usuario> listar() {

        return authService.listar();
    }

    @GetMapping("/buscar/{email}")
    public Usuario buscarPorEmail(@PathVariable String email) {
        return authService.buscarPorEmail(email);
    }

    @PostMapping("/criar")
    public UsuarioResponse criar(@RequestBody CriarUsuarioRequest criarUsuarioRequest) {
        return authService.createUser(criarUsuarioRequest);
    }

    @PutMapping("/atualizar")
    public UsuarioResponse atualizar(@RequestBody AtualizarUsuarioRequest atualizarUsuarioRequest) {
        return authService.updateUsuario(atualizarUsuarioRequest);
    }

    @DeleteMapping("/deletar/{email}")
    public UsuarioResponse deletar(@PathVariable String email) {
        return authService.deleteUsuario(email);
    }

}
