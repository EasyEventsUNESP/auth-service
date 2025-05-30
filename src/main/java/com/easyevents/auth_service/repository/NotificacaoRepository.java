package com.easyevents.auth_service.repository;

import com.easyevents.auth_service.domain.model.NotificacaoModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificacaoRepository extends MongoRepository<NotificacaoModel, String> {
}
