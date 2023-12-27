package io.products.CashFlowGo.service.serviceImpl;


import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.internal.connection.Time;
import io.products.CashFlowGo.exception.CurrencyConversionException;
import io.products.CashFlowGo.model.CurrencyConversion;
import io.products.CashFlowGo.model.request.Record;
import io.products.CashFlowGo.model.request.enums.Currency;
import io.products.CashFlowGo.model.response.RecordResponse;
import io.products.CashFlowGo.repository.MongoRepository;
import io.products.CashFlowGo.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final MongoRepository mongoRepository;
    private final RestTemplate restTemplate;
    private final CurrencyConversion currencyConversion;

    @Autowired
    public TransactionServiceImpl(MongoRepository mongoRepository, RestTemplate restTemplate, CurrencyConversion currencyConversion){
        this.mongoRepository = mongoRepository;
        this.restTemplate = restTemplate;
        this.currencyConversion = currencyConversion;
    }
    @Override
    public Mono<ResponseEntity<RecordResponse>> registerTransaction(Record record) {
        convertCurrency(record);
        mongoRepository.save(record);
        RecordResponse recordResponse = new RecordResponse();
        recordResponse.setStatus(HttpStatus.CREATED);
        recordResponse.setMessage("Successfully created the transaction");
        return Mono.just(ResponseEntity.ok().body(recordResponse));
    }

    @Override
    public Mono<ResponseEntity<RecordResponse>> getTransactionDetails() {
        Flux<Record> recordList = mongoRepository.findAll();
        RecordResponse recordResponse = new RecordResponse();
        //recordResponse.setRecordList(recordList);
        recordResponse.setStatus(HttpStatus.OK);
        recordResponse.setMessage("Successfully fetched the transactions between the given time range");
        return Mono.just(ResponseEntity.ok().body(recordResponse));
    }

    public void convertCurrency(Record record) {
        Currency currency = record.getCurrency();
        Float amount = record.getPrice();
        JsonNode jsonNode = restTemplate.getForObject(currencyConversion.getCurrencyConversionUrl(), JsonNode.class);
        assert jsonNode != null;
        if(Objects.nonNull(jsonNode.get("success")) && (Objects.nonNull(jsonNode.get("rates").get("INR"))) && !jsonNode.get("success").booleanValue() && jsonNode.get("rates").get("INR").floatValue() != 0){
            throw new CurrencyConversionException("Failed to register transaction as currency conversion failed!");
        }
        Float conversionRate = jsonNode.get("rates").get("INR").floatValue();
        if(currency.equals(Currency.INR)){
            record.setInrPrice(amount);
            record.setUsdPrice(amount/conversionRate);
            return;
        }
        record.setUsdPrice(amount);
        record.setInrPrice(amount*conversionRate);
    }
}
