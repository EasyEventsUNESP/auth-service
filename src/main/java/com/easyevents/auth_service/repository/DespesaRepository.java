package com.easyevents.auth_service.repository;

import com.easyevents.auth_service.domain.model.Despesa;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DespesaRepository extends MongoRepository<Despesa, String> {
}
