package com.easyevents.auth_service.repository;

import com.easyevents.auth_service.domain.model.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {
}
