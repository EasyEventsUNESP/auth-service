package com.easyevents.auth_service.service;

import com.easyevents.auth_service.domain.dto.request.AtualizarUsuarioRequest;
import com.easyevents.auth_service.domain.dto.request.CriarUsuarioRequest;
import com.easyevents.auth_service.domain.dto.request.LoginRequest;
import com.easyevents.auth_service.domain.dto.response.LoginResponse;
import com.easyevents.auth_service.domain.dto.response.UsuarioResponse;
import com.easyevents.auth_service.domain.enumerator.Provedor;
import com.easyevents.auth_service.domain.model.UsuarioModel;
import com.easyevents.auth_service.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
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

    public ResponseEntity<List<UsuarioModel>> listar() {
        return ResponseEntity.status(HttpStatus.FOUND).body(usuarioRepository.findAll());
    }

    public ResponseEntity<UsuarioModel> buscarPorEmail(String email) {
        return ResponseEntity.status(HttpStatus.FOUND).body(usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado")));
    }

    public ResponseEntity<UsuarioResponse> criarUsuario(CriarUsuarioRequest criarUsuarioRequest) {

        if (usuarioRepository.findByEmail(criarUsuarioRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Usuário já cadastrado com o e-mail: " + criarUsuarioRequest.getEmail());
        }

        String senhaPlana = criarUsuarioRequest.getSenha();
        // Codifica a senha normalmente com BCrypt
        String hashSenhaBCrypt = passwordEncoder.encode(senhaPlana);

        // Adiciona o prefixo {bcrypt} ao hash antes de salvar
        String senhaParaArmazenar = "{bcrypt}" + hashSenhaBCrypt;

        UsuarioModel novoUsuario = UsuarioModel.builder()
                .nome(criarUsuarioRequest.getNome())
                .senha(senhaParaArmazenar) // Salva a senha com o prefixo
                .email(criarUsuarioRequest.getEmail())
                .admin(true)
                .criacao(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .provedor(Provedor.LOCAL)
                .build();

        usuarioRepository.insert(novoUsuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                UsuarioResponse.builder()
                        .email(novoUsuario.getEmail())
                        .nome(novoUsuario.getNome())
                        .responseMessage("Usuário criado com sucesso!")
                        .build()
        );
    }

    public ResponseEntity<UsuarioResponse> atualizarUsuario(AtualizarUsuarioRequest atualizarUsuarioRequest) {
        UsuarioModel usuarioModel = usuarioRepository.findByEmail(atualizarUsuarioRequest.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado com o e-mail: " + atualizarUsuarioRequest.getEmail()));

        boolean modificado = false;

        if (atualizarUsuarioRequest.getNovoNome() != null && !atualizarUsuarioRequest.getNovoNome().trim().isEmpty()) {
            if (!atualizarUsuarioRequest.getNovoNome().equals(usuarioModel.getNome())) {
                usuarioModel.setNome(atualizarUsuarioRequest.getNovoNome().trim());
                modificado = true;
            }
        }

        // Atualiza a senha, SE fornecida, e a codifica com o prefixo
        if (atualizarUsuarioRequest.getNovaSenha() != null && !atualizarUsuarioRequest.getNovaSenha().isEmpty()) {
            String novaSenhaPlana = atualizarUsuarioRequest.getNovaSenha();
            String novoHashSenhaBCrypt = passwordEncoder.encode(novaSenhaPlana);
            // Adiciona o prefixo {bcrypt} ao hash antes de salvar
            String novaSenhaParaArmazenar = "{bcrypt}" + novoHashSenhaBCrypt;
            if (!novaSenhaParaArmazenar.equals(usuarioModel.getSenha())) { // Evita salvar se a senha for a mesma
                usuarioModel.setSenha(novaSenhaParaArmazenar);
                modificado = true;
            }
        }

        // Lógica para atualizar a flag 'admin'
        // Verifica se a requisição forneceu um valor para 'admin'
        if (atualizarUsuarioRequest.getAdmin() != null) {
            // Verifica se o valor fornecido é diferente do valor atual para evitar atualização desnecessária
            if (atualizarUsuarioRequest.getAdmin() != usuarioModel.getAdmin()) {
                usuarioModel.setAdmin(atualizarUsuarioRequest.getAdmin());
                modificado = true;
            }
        }

        if (modificado) {
            usuarioModel.setUpdatedAt(LocalDateTime.now());
            usuarioRepository.save(usuarioModel);
        }

        // Retorna a resposta, incluindo o status de admin
        return ResponseEntity.status(HttpStatus.OK)
                .body(UsuarioResponse.builder()
                        .email(usuarioModel.getEmail())
                        .nome(usuarioModel.getNome())
                        .admin(usuarioModel.getAdmin()) // <-- Inclua o status de admin aqui
                        .responseMessage(modificado ? "Perfil atualizado com sucesso!" : "Nenhuma alteração fornecida ou dados são os mesmos.")
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

    public ResponseEntity<UsuarioResponse> recuperarSenha(String email) {
        UsuarioModel usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com o e-mail: " + email));

        // Gera uma senha aleatória de 12 caracteres
        String novaSenhaTemporaria = gerarSenhaAleatoria();

        // Encripta a senha usando BCrypt e adiciona o prefixo
        String hashSenhaBCrypt = passwordEncoder.encode(novaSenhaTemporaria);
        String senhaParaArmazenar = "{bcrypt}" + hashSenhaBCrypt;

        // Atualiza a senha no modelo do usuário
        usuario.setSenha(senhaParaArmazenar);
        usuario.setUpdatedAt(LocalDateTime.now());

        // Salva no banco de dados
        usuarioRepository.save(usuario);

        logger.info("Nova senha temporária gerada para o usuário: {}", email);

        return ResponseEntity.status(HttpStatus.OK)
                .body(UsuarioResponse.builder()
                        .email(usuario.getEmail())
                        .nome(usuario.getNome())
                        .responseMessage(novaSenhaTemporaria) // Retorna a senha temporária não encriptada
                        .build());
    }

    private String gerarSenhaAleatoria() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder senha = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < 12; i++) {
            int index = random.nextInt(caracteres.length());
            senha.append(caracteres.charAt(index));
        }

        return senha.toString();
    }
}
