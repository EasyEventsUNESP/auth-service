package com.easyevents.auth_service.service;

import com.easyevents.auth_service.domain.dto.request.AtualizarUsuarioRequest;
import com.easyevents.auth_service.domain.dto.request.CriarUsuarioRequest;
import com.easyevents.auth_service.domain.dto.request.LoginRequest;
import com.easyevents.auth_service.domain.dto.response.UsuarioResponse;
import com.easyevents.auth_service.domain.enumerator.Provedor;
import com.easyevents.auth_service.domain.model.UsuarioModel;
import com.easyevents.auth_service.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
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

    public ResponseEntity<UsuarioResponse> criarUsuario(CriarUsuarioRequest criarUsuarioRequest){

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
                .provedor(Provedor.LOCAL) // Define o provedor como LOCAL para usuários criados localmente
                .build());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                UsuarioResponse.builder()
                        .email(criarUsuarioRequest.getEmail())
                        .nome(criarUsuarioRequest.getNome())
                        .responseMessage("Usuário criado com sucesso!")
                        .build()
        );
    }

    public ResponseEntity<UsuarioResponse> atualizarUsuario(AtualizarUsuarioRequest atualizarUsuarioRequest) {

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

    public ResponseEntity<UsuarioResponse> deletarUsuario(String email) {
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

    /**
     * Processa o login de um usuário via OAuth2 (ex: Google).
     * Procura o usuário pelo email. Se existir, atualiza os dados (ex: nome, data de atualização).
     * Se não existir, cria um novo usuário com os dados do provedor OAuth2.
     * Para usuários OAuth2, a senha local não é gerenciada por este fluxo.
     *
     * @param email Email do usuário fornecido pelo provedor OAuth2.
     * @param nome Nome completo do usuário fornecido pelo provedor OAuth2.
     * @param attributes Atributos adicionais do provedor OAuth2 (para uso futuro, log ou outros campos).
     * @return O UsuarioModel salvo ou atualizado.
     * @throws IllegalArgumentException se o email for nulo ou vazio.
     */
    public UsuarioModel processarLoginOAuth2(String email, String nome, Map<String, Object> attributes, Provedor provedor) {
        // Validação básica do email
        if (email == null || email.trim().isEmpty()) {
            logger.error("AuthService: Email nulo ou vazio recebido para login OAuth2. Atributos: {}", attributes);
            throw new IllegalArgumentException("Email não pode ser nulo ou vazio para processar login OAuth2.");
        }
        Optional<UsuarioModel> usuarioExistenteOpt = usuarioRepository.findByEmail(email);

        UsuarioModel usuario;
        if (usuarioExistenteOpt.isPresent()) {
            usuario = usuarioExistenteOpt.get();
            logger.info("AuthService: Usuário OAuth2 existente encontrado por email [{}]. Atualizando informações.", email);

            if (nome != null && !nome.trim().isEmpty() && !nome.equals(usuario.getNome())) {
                usuario.setNome(nome);
            }
            usuario.setUpdatedAt(LocalDateTime.now());
            usuario.setProvedor(provedor); // Atualiza o provedor
        } else {
            logger.info("AuthService: Novo usuário OAuth2 com email [{}]. Criando no banco de dados.", email);
            usuario = UsuarioModel.builder()
                    .email(email)
                    .nome(nome)
                    .criacao(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .provedor(provedor) // Define o provedor
                    .build();
        }

        return usuarioRepository.save(usuario);
    }
}
