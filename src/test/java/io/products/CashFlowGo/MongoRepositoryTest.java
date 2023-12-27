package io.products.CashFlowGo;

import io.products.CashFlowGo.model.Customer;
import io.products.CashFlowGo.model.request.Record;
import io.products.CashFlowGo.model.request.enums.Currency;
import io.products.CashFlowGo.model.request.enums.TransactionType;
import io.products.CashFlowGo.repository.MongoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@DataMongoTest
@ExtendWith(SpringExtension.class)
public class MongoRepositoryTest {

    @Autowired
    private MongoRepository repository;

    @Test
    public void shouldSaveSingleTransaction() {
        Record record = new Record("12345", null, null, "Mobile Phone", new Customer("Suresh", "123234", "suresh@gmail.com"), LocalDateTime.now(), Currency.INR, TransactionType.credit);
        Publisher<Record> setup = repository.deleteAll().thenMany(repository.save(record));
        StepVerifier
                .create(setup)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void shouldSaveUser() {
        String ID = "12345";
        Record record = new Record("12345", null, null, "Mobile Phone", new Customer("Suresh", "123234", "suresh@gmail.com"), LocalDateTime.now(), Currency.INR, TransactionType.credit);
        Publisher<Record> setup = repository.deleteAll().then(repository.save(record));
        Mono<Record> find = repository.findById(ID);
        Publisher<Record> composite = Mono
                .from(setup)
                .then(find);
        StepVerifier
                .create(composite)
                .consumeNextWith(record1 -> {
                    assertNotNull(record1.getId());
                    assertEquals(record1.getDescription(), "Mobile Phone");
                    assertEquals(record1.getCurrency(), Currency.INR);
                    assertEquals(record1.getTransactionType(), TransactionType.credit);
                })
                .verifyComplete();
    }
}

