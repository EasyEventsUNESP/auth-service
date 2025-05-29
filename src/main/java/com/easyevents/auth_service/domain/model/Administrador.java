package com.easyevents.auth_service.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "administrador")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Administrador {

    @Id
    private Usuario administradorId;

}
