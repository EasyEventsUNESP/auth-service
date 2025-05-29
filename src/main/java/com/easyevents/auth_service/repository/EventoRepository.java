package com.easyevents.auth_service.repository;

import com.easyevents.auth_service.domain.model.Evento;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventoRepository extends MongoRepository<Evento, String> {
}
