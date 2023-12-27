package io.products.CashFlowGo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.products.CashFlowGo.model.request.Record;
import io.products.CashFlowGo.model.response.RecordResponse;
import io.products.CashFlowGo.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.sql.Time;

@RestController
@CrossOrigin
public class TransactionController {
    TransactionService transactionService;
    RestTemplate restTemplate;

    @Autowired
    public TransactionController(TransactionService transactionService, RestTemplate restTemplate){
        this.transactionService = transactionService;
        this.restTemplate = restTemplate;
    }

    @PostMapping("/v1/create/transact")
    public Mono<ResponseEntity<RecordResponse>> registerTransaction(@RequestBody Record record) throws JsonProcessingException {
        return transactionService. registerTransaction(record);
    }
    @GetMapping("v1/transact")
    public Mono<ResponseEntity<RecordResponse>> getAllTransactions(Record record){
         return transactionService.getTransactionDetails();
    }
}
