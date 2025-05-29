package com.easyevents.auth_service.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "notificacao")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notificacao {

    @Id
    private String id;
    private String assunto;
    private Intensidade intensidade;
    private TipoNotificacao tipoNotificacao;
    private LocalDateTime dataHora;
    private Usuario remetente;
    private String mensagem;
    private List<Pessoa> destinatarios;

}
