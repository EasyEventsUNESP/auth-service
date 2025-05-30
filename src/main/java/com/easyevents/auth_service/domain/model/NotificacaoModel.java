package com.easyevents.auth_service.domain.model;

import com.easyevents.auth_service.domain.enumerator.Intensidade;
import com.easyevents.auth_service.domain.enumerator.TipoNotificacao;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "notificacao")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificacaoModel {

    @Id
    private String id;
    private String assunto;
    private Intensidade intensidade;
    private TipoNotificacao tipoNotificacao;
    private LocalDateTime dataHora;
    private String remetente;
    private String mensagem;
    private List<String> destinatariosId;

}
