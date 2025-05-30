package com.easyevents.auth_service.service;

import com.easyevents.auth_service.domain.enumerator.Provedor;
import com.easyevents.auth_service.domain.model.UsuarioModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomOidcUserService extends OidcUserService {

    private static final Logger logger = LoggerFactory.getLogger(CustomOidcUserService.class);

    private final AuthService authService;

    @Autowired
    public CustomOidcUserService(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        Map<String, Object> attributes = oidcUser.getAttributes();
        String email = oidcUser.getEmail();
        String nomeCompleto = oidcUser.getFullName();

        // Lógica para tentar obter o nome completo de diferentes atributos se getFullName() for nulo
        if (nomeCompleto == null && attributes.containsKey("name")) {
            nomeCompleto = (String) attributes.get("name");
        } else if (nomeCompleto == null && oidcUser.getGivenName() != null) {
            nomeCompleto = oidcUser.getGivenName() + (oidcUser.getFamilyName() != null ? " " + oidcUser.getFamilyName() : "");
        }

        // Se ainda assim for nulo, você pode definir um nome padrão ou lançar um erro se for obrigatório
        if (nomeCompleto == null) {
            // Se o nome completo não puder ser determinado, use o email como fallback
            nomeCompleto = email;
            logger.warn("Não foi possível determinar o nome completo para o usuário com email: {}. Usando o email como nome padrão.", email);
            if (email == null) { // Email também não pode ser nulo para o processarLoginOAuth2
                OAuth2Error oauth2Error = new OAuth2Error("missing_user_info", "Informações essenciais do usuário (email) não foram fornecidas pelo provedor.", null);
                throw new OAuth2AuthenticationException(oauth2Error, "Email não fornecido pelo provedor OIDC.");
            }
            nomeCompleto = email; // Ou alguma lógica para um nome padrão
        }


        logger.info("Usuário OAuth2 (OIDC) autenticado: Email [{}], Nome [{}], Provedor a ser usado: GOOGLE", email, nomeCompleto);

        try {
            UsuarioModel usuarioProcessado = authService.processarLoginOAuth2(
                    email,
                    nomeCompleto,
                    attributes,
                    Provedor.GOOGLE
            );
            logger.info("Usuário processado/salvo via AuthService: Email [{}], Provedor [{}]",
                    usuarioProcessado.getEmail(), usuarioProcessado.getProvedor());


            // Você pode retornar o oidcUser original.
            // Se precisar de informações do UsuarioModel no Principal, pode criar um OidcUser customizado aqui
            // que envolva tanto o oidcUser quanto seu usuarioProcessado, ou adicione atributos ao oidcUser.
            // Exemplo simples de adicionar o ID do seu banco ao OidcUser (se relevante):
            // Map<String, Object> newAttributes = new HashMap<>(attributes);
            // newAttributes.put("appUserId", usuarioProcessado.getId()); // Supondo que UsuarioModel tem getId()
            // return new DefaultOidcUser(oidcUser.getAuthorities(), oidcUser.getIdToken(), oidcUser.getUserInfo(), newAttributes, "email");

        } catch (IllegalArgumentException iae) { // Captura específica para erros de validação do AuthService
            logger.error("Argumento inválido ao processar usuário OAuth2 (email: {}): {}", email, iae.getMessage(), iae);
            OAuth2Error oauth2Error = new OAuth2Error("invalid_user_data", "Dados do usuário inválidos ou ausentes: " + iae.getMessage(), null);
            throw new OAuth2AuthenticationException(oauth2Error, iae); // Passa a exceção original como causa

        } catch (Exception e) {
            logger.error("Erro genérico ao chamar AuthService para processar usuário OAuth2 (email: {}): {}", email, e.getMessage(), e);
            throw new OAuth2AuthenticationException(new OAuth2Error("auth_service_error", "Erro interno ao processar informações do usuário.", null), e); // Passa a exceção original como causa
        }

        return oidcUser; // Retorna o usuário OIDC padrão (ou customizado)
    }
}