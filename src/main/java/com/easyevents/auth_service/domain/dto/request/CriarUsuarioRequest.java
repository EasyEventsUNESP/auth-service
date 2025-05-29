package com.easyevents.auth_service.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CriarUsuarioRequest {

    private String nome;
    private String senha;
    private String email;
}
