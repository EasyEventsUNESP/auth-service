package com.easyevents.auth_service.service;

import com.easyevents.auth_service.domain.dto.request.UsuarioRequest;
import com.easyevents.auth_service.domain.dto.response.UsuarioResponse;
import com.easyevents.auth_service.domain.model.Usuario;
import com.easyevents.auth_service.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {


    @Autowired
    private UsuarioRepository usuarioRepository;
    public UsuarioResponse createUser(UsuarioRequest usuarioRequest){

        usuarioRepository.insert(
                Usuario.builder()
                .nome(usuarioRequest.getNome())
                .senha(usuarioRequest.getSenha())
                .email(usuarioRequest.getEmail())
                .criacao(LocalDateTime.now())
                .build()
        );

        return UsuarioResponse.builder()
                .email(usuarioRequest.getEmail())
                .nome(usuarioRequest.getNome())
                .responseMessage("Usuário criado com sucesso!")
                .build();
    }
}
