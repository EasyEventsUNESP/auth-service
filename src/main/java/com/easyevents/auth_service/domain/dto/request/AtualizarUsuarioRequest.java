package com.easyevents.auth_service.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AtualizarUsuarioRequest {

    private String email;
    private String novoNome;
    private String novaSenha;
    private String novoEmail;
    private Boolean admin;
}
