package com.easyevents.auth_service.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "evento")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventoModel {

    @Id
    private String id;
    private String nome;
    private String descricao;
    private String local;
    private String emailOrganizador;
    private LocalDateTime horaInicio;
    private LocalDateTime horaFim;
    private LocalDateTime updatedAt;
    private String orcamentoId;

}
