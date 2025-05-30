package com.easyevents.auth_service.repository;

import com.easyevents.auth_service.domain.model.UsuarioModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UsuarioRepository extends MongoRepository<UsuarioModel, String> {

    Optional<UsuarioModel> findByEmail(String email);
}
