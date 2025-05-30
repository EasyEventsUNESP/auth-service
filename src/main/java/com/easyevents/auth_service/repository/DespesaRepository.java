package com.easyevents.auth_service.repository;

import com.easyevents.auth_service.domain.model.DespesaModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DespesaRepository extends MongoRepository<DespesaModel, String> {
}
