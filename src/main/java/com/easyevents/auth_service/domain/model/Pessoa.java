package com.easyevents.auth_service.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "pessoa")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pessoa {

    @Id
    private String id;
    private String nome;
    private String email;

}
