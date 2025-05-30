package com.easyevents.auth_service.service;

import com.easyevents.auth_service.domain.dto.request.AtualizarUsuarioRequest;
import com.easyevents.auth_service.domain.dto.request.CriarUsuarioRequest;
import com.easyevents.auth_service.domain.dto.request.LoginRequest;
import com.easyevents.auth_service.domain.dto.response.UsuarioResponse;
import com.easyevents.auth_service.domain.model.UsuarioModel;
import com.easyevents.auth_service.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthService() {
    }

    public ResponseEntity<List<UsuarioModel>> listar() {
        return ResponseEntity.status(HttpStatus.FOUND).body(usuarioRepository.findAll());
    }

    public ResponseEntity<UsuarioModel> buscarPorEmail(String email) {
        return ResponseEntity.status(HttpStatus.FOUND).body(usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado")));
    }

    public ResponseEntity<UsuarioResponse> createUser(CriarUsuarioRequest criarUsuarioRequest){

        // Verifica se o usuário já existe
        if (usuarioRepository.findByEmail(criarUsuarioRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Usuário já cadastrado");
        }

        criarUsuarioRequest.setSenha(passwordEncoder.encode(criarUsuarioRequest.getSenha()));

        usuarioRepository.insert(UsuarioModel.builder()
                .nome(criarUsuarioRequest.getNome())
                .senha(criarUsuarioRequest.getSenha())
                .email(criarUsuarioRequest.getEmail())
                .criacao(LocalDateTime.now())
                .build());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                UsuarioResponse.builder()
                        .email(criarUsuarioRequest.getEmail())
                        .nome(criarUsuarioRequest.getNome())
                        .responseMessage("Usuário criado com sucesso!")
                        .build()
        );
    }

    public ResponseEntity<UsuarioResponse> updateUsuario(AtualizarUsuarioRequest atualizarUsuarioRequest) {

        UsuarioModel usuarioModel = usuarioRepository.findByEmail(atualizarUsuarioRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (atualizarUsuarioRequest.getNovoNome() != null && !atualizarUsuarioRequest.getNovoNome().isEmpty()) {
            usuarioModel.setNome(atualizarUsuarioRequest.getNovoNome());
        }

        if (atualizarUsuarioRequest.getNovaSenha() != null && !atualizarUsuarioRequest.getNovaSenha().isEmpty()) {
            usuarioModel.setSenha(atualizarUsuarioRequest.getNovaSenha());
        }

        if (atualizarUsuarioRequest.getNovoEmail() != null && !atualizarUsuarioRequest.getNovoEmail().isEmpty()) {
            usuarioModel.setEmail(atualizarUsuarioRequest.getNovoEmail());
        }

        usuarioModel.setUpdatedAt(LocalDateTime.now());

        usuarioRepository.save(usuarioModel);

        return ResponseEntity.status(HttpStatus.OK)
                .body(UsuarioResponse.builder()
                        .email(usuarioModel.getEmail())
                        .nome(usuarioModel.getNome())
                        .responseMessage("Perfil atualizado com sucesso!")
                        .build()
                );
    }

    public ResponseEntity<UsuarioResponse> deleteUsuario(String email) {
        UsuarioModel usuarioModel = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        usuarioRepository.delete(usuarioModel);

        return ResponseEntity.status(HttpStatus.OK).body(
                UsuarioResponse.builder()
                        .email(usuarioModel.getEmail())
                        .nome(usuarioModel.getNome())
                        .responseMessage("Usuário deletado com sucesso.")
                        .build()
        );
    }

    public ResponseEntity<UsuarioResponse> login(LoginRequest loginRequest){
        UsuarioModel usuarioModel = usuarioRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        boolean validPassword = passwordEncoder.matches(loginRequest.getSenha(), usuarioModel.getSenha());
        HttpStatus status = validPassword ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;

        return ResponseEntity.status(status)
                .body(UsuarioResponse.builder()
                        .email(usuarioModel.getEmail())
                        .nome(usuarioModel.getNome())
                        .responseMessage(validPassword ? "Login realizado com sucesso!" : "Senha incorreta!")
                        .build()
                );
    }
}
