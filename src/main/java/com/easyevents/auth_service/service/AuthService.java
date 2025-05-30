package com.easyevents.auth_service.service;

import com.easyevents.auth_service.domain.dto.request.AtualizarUsuarioRequest;
import com.easyevents.auth_service.domain.dto.request.CriarUsuarioRequest;
import com.easyevents.auth_service.domain.dto.response.UsuarioResponse;
import com.easyevents.auth_service.domain.model.Usuario;
import com.easyevents.auth_service.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public UsuarioResponse createUser(CriarUsuarioRequest criarUsuarioRequest){

        // Verifica se o usuário já existe
        if (usuarioRepository.findByEmail(criarUsuarioRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Usuário já cadastrado");
        }

        usuarioRepository.insert(Usuario.builder()
                .nome(criarUsuarioRequest.getNome())
                .senha(criarUsuarioRequest.getSenha())
                .email(criarUsuarioRequest.getEmail())
                .criacao(LocalDateTime.now())
                .build());

        return UsuarioResponse.builder()
                .email(criarUsuarioRequest.getEmail())
                .nome(criarUsuarioRequest.getNome())
                .responseMessage("Usuário criado com sucesso!")
                .build();
    }

    public UsuarioResponse updateUsuario(AtualizarUsuarioRequest atualizarUsuarioRequest) {

        Usuario usuario = usuarioRepository.findByEmail(atualizarUsuarioRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (atualizarUsuarioRequest.getNovoNome() != null && !atualizarUsuarioRequest.getNovoNome().isEmpty()) {
            usuario.setNome(atualizarUsuarioRequest.getNovoNome());
        }

        if (atualizarUsuarioRequest.getNovaSenha() != null && !atualizarUsuarioRequest.getNovaSenha().isEmpty()) {
            usuario.setSenha(atualizarUsuarioRequest.getNovaSenha());
        }

        if (atualizarUsuarioRequest.getNovoEmail() != null && !atualizarUsuarioRequest.getNovoEmail().isEmpty()) {
            usuario.setEmail(atualizarUsuarioRequest.getNovoEmail());
        }

        usuario.setUpdatedAt(LocalDateTime.now());

        usuarioRepository.save(usuario);

        return UsuarioResponse.builder()
                .email(usuario.getEmail())
                .nome(usuario.getNome())
                .responseMessage("Perfil atualizado com sucesso!")
                .build();
    }

    public UsuarioResponse deleteUsuario(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuarioRepository.delete(usuario);

        return UsuarioResponse.builder()
                .email(usuario.getEmail())
                .nome(usuario.getNome())
                .responseMessage("Usuário deletado com sucesso.")
                .build();
    }
}
