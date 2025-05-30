package com.easyevents.auth_service.repository;

import com.easyevents.auth_service.domain.model.ConvidadoModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConvidadoRepository extends MongoRepository<ConvidadoModel, String> {
}
