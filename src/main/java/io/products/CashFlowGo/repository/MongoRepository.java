package io.products.CashFlowGo.repository;

import io.products.CashFlowGo.model.request.Record;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;


import java.time.LocalDateTime;
import java.util.List;

/**
 * Reactive MongoDB repository for handling operations related to the Record entity.
 */
@Repository
public interface MongoRepository extends ReactiveMongoRepository<Record, String> {

    /**
     * Custom query method to find records with timestamps between the specified range.
     *
     * @param from The start timestamp of the range.
     * @param to   The end timestamp of the range.
     * @return A Flux of records with timestamps between the specified range.
     */
    @Query("{'timestamp' : {$gte : ?0, $lte : ?1 }}")
    Flux<Record> findByTimestampBetween(LocalDateTime from, LocalDateTime to);
}
