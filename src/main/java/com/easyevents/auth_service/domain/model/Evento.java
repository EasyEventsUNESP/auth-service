package com.easyevents.auth_service.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "evento")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Evento {

    @Id
    private String id;
    private String nome;
    private String descricao;
    private String local;
    private LocalDateTime hora_inicio;
    private LocalDateTime hora_fim;
    private LocalDateTime updatedAt;
    private Orcamento orcamento;
    private List<Convidado> convidados;
    private List<Notificacao> notificacoes;

}
