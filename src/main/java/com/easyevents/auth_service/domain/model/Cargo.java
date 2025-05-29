package com.easyevents.auth_service.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

public enum Cargo {
    CRIADOR,
    ORGANIZADOR,
    COLABORADOR
}
