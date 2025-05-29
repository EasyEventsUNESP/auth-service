package com.easyevents.auth_service.repository;

import com.easyevents.auth_service.domain.model.Administrador;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AdministradorRepository extends MongoRepository<Administrador, String> {
}
