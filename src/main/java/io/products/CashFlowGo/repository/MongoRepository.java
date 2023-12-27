package io.products.CashFlowGo.repository;

import io.products.CashFlowGo.model.request.Record;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;


import java.util.List;


public interface MongoRepository extends ReactiveMongoRepository<Record, String> {
}
