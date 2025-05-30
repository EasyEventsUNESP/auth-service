package com.easyevents.auth_service.repository;

import com.easyevents.auth_service.domain.model.EventoModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventoRepository extends MongoRepository<EventoModel, String> {
}
