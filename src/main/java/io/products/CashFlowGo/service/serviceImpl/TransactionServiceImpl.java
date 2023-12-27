package io.products.CashFlowGo.service.serviceImpl;


import com.fasterxml.jackson.databind.JsonNode;
import io.products.CashFlowGo.exception.CurrencyConversionException;
import io.products.CashFlowGo.exception.NoTransactionsFoundException;
import io.products.CashFlowGo.exception.TransactionProcessingException;
import io.products.CashFlowGo.model.CurrencyConversion;
import io.products.CashFlowGo.model.request.Record;
import io.products.CashFlowGo.model.request.RecordFetchRequest;
import io.products.CashFlowGo.model.request.enums.Currency;
import io.products.CashFlowGo.model.request.enums.TimeStampType;
import io.products.CashFlowGo.model.request.enums.TransactionType;
import io.products.CashFlowGo.model.response.RecordResponse;
import io.products.CashFlowGo.repository.MongoRepository;
import io.products.CashFlowGo.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation of the TransactionService interface for managing financial transactions.
 */
@Service
@Slf4j
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

    /**
     * @Usage Register the transaction of CashFlowGo user into both USD and INR and saves it in database.
     * @Functional Saves a transaction by converting its currency into both USD and INR, saving the record to the MongoDB repository,
     * and returning a response entity containing the saved record information.
     *
     * @param record The transaction record to be registered.
     * @return A Mono containing the ResponseEntity with the RecordResponse, representing the result of the registration.
     *         The ResponseEntity will have a status of HttpStatus.CREATED if the transaction is successfully registered,
     *         and the RecordResponse will contain information about the saved transaction.
     * @throws CurrencyConversionException If an error occurs during currency conversion.
     * @throws TransactionProcessingException If an error occurs during saving the exception.
     */
    @Override
    public Mono<ResponseEntity<RecordResponse>>registerTransaction(Record record) {
        if(record.getPrice() < 0){
            log.error("Transaction amount is less than 0");
            throw new TransactionProcessingException("Amount cannot be less than 0");
        }
        // Convert the currency into USD and INR with current exchange rates and set it into record
        if(TransactionType.debit.equals(record.getTransactionType())){
            record.setPrice(-1*record.getPrice());
        }
        convertCurrency(record);
        log.info("Successfully converted the currency and set into record");

        try {
            // Saves the Response into the mongo repository and bind the response into RecordResponse POJO.
            return mongoRepository.save(record)
                    .flatMap(savedRecord -> {
                        // Create a RecordResponse with the saved record information
                        RecordResponse recordResponse = new RecordResponse();
                        recordResponse.setRecordList(Collections.singletonList(savedRecord));
                        recordResponse.setStatus(HttpStatus.CREATED);
                        recordResponse.setMessage("Successfully registered the transactions!");
                        // Return a ResponseEntity with the RecordResponse
                        return Mono.just(ResponseEntity.ok().body(recordResponse));
                    })
                    .doOnSuccess(responseEntity -> log.info("Successfully saved the transaction in the database"))
                    .switchIfEmpty(Mono.just(ResponseEntity.ok().body(new RecordResponse())))
                    .onErrorMap(throwable -> {
                        // If an exception occurs during the process, map it to a TransactionProcessingException
                        throw new TransactionProcessingException("Error occurred while saving with exception: " + throwable.getLocalizedMessage());
                    });
        }
        // If any unknown exception occurs, bind the response into TransactionProcessingException.
        catch (Exception e){
            throw new TransactionProcessingException("Failed to process the transaction with exception: " + e.getMessage());
        }
    }

    /**
     * @Usage CashFlowGo User can fetch the transaction using this API, all the saved transaction can be fetched
     * based on quick filters such as Last 24 hour/Last 7 days/Last 30 days or a custom date range.
     *
     * @Functional Retrieves transaction details based on the provided {@link RecordFetchRequest}, which contains TimeStampType,
     * which contains quick filters or user can select CUSTOM and filter out all based on custom date. Custom date supports
     * only Epochs.
     *
     * @param recordFetchRequest The request containing information about the time range and type for fetching records.
     * @return A {@link Mono} containing a {@link ResponseEntity} with a {@link RecordResponse} as the body.
     *         The response includes a list of records within the specified time range, along with status and message.
     *         The status is set to {@link HttpStatus#OK} if the operation is successful.
     */
    @Override
    public Mono<ResponseEntity<RecordResponse>> getTransactionDetails(RecordFetchRequest recordFetchRequest) {

        // Extracts the Current Time Stamp and time stamp type from RecordFetchRequest
        LocalDateTime currentTimeStamp = recordFetchRequest.getCurrentTimeStamp();
        TimeStampType timeStampType = recordFetchRequest.getTimeStampType();
        Flux<Record> recordFetched;
        Flux<Float> totalSumINR;
        Flux<Float> totalSumUSD;
        // If TimeStampType is LAST_24_hour then fetch the records between current time to last 24 hour.
        if(timeStampType.equals(TimeStampType.LAST_24_HOUR)){
            LocalDateTime decreasedTime = currentTimeStamp.minusHours(24);
            recordFetched = mongoRepository.findByTimestampBetween(decreasedTime, currentTimeStamp);
        // If TimeStampType is LAST_WEEK then fetch the records between current time to last 7 days.
        } else if (timeStampType.equals(TimeStampType.LAST_WEEK)) {
            LocalDateTime decreasedTime = currentTimeStamp.minusDays(7);
            recordFetched = mongoRepository.findByTimestampBetween(decreasedTime, currentTimeStamp);
        // If TimeStampType is LAST_WEEK then fetch the records between current time to last 30 days.
        } else if (timeStampType.equals(TimeStampType.LAST_MONTH)) {
            LocalDateTime decreasedTime = currentTimeStamp.minusDays(30);
            recordFetched = mongoRepository.findByTimestampBetween(decreasedTime, currentTimeStamp);
        }
        // If TimeStampType is CUSTOM then fetches the records between given time ranges. Custom time ranges are in Epoch,
        // so conversion is required to LocalDateTime object.
        else {
            LocalDateTime from = LocalDateTime.ofEpochSecond(recordFetchRequest.getEpochFrom(), 0, ZoneOffset.UTC);
            LocalDateTime to = LocalDateTime.ofEpochSecond(recordFetchRequest.getEpochTo(), 0, ZoneOffset.UTC);
            recordFetched = mongoRepository.findByTimestampBetween(from, to);
        }
        totalSumINR = recordFetched
                .flatMap(record -> Flux.just(record.getInrPrice())
                        .reduce(0f, Float::sum));

        totalSumUSD = recordFetched
                .flatMap(record -> Flux.just(record.getUsdPrice())
                        .reduce(0f, Float::sum));

// Combine the results of totalSumINR and totalSumUSD Fluxes
        Flux<Tuple2<Float, Float>> mergedSums = Flux.zip(totalSumINR, totalSumUSD);

// Collect the list of response fetch and bind into Mono<ResponseEntity<RecordResponse>>
        return recordFetched.collectList()
                .zipWith(mergedSums.collectList())
                .map(tuple -> {
                    List<Record> records = tuple.getT1();
                    List<Tuple2<Float, Float>> sumsTupleList = tuple.getT2();

                    // Check if either records or sumsTupleList is empty
                    if (records.isEmpty() || sumsTupleList.isEmpty()) {
                        throw new NoTransactionsFoundException("No Transaction Found within current time frame");
                    }

                    // Extracting List<Float> from List<Tuple2<Float, Float>>
                    // This list contains all the inr at even place and all usd at odd placese
                    List<Float> sumsList = sumsTupleList
                            .stream()
                            .flatMap(t -> Stream.of(t.getT1(), t.getT2()))
                            .toList();

                    RecordResponse recordResponse = new RecordResponse();
                    recordResponse.setRecordList(records);

                    log.info("List of float elements present - "+sumsList);
                    Float totalINR = 0f;
                    Float totalUSD = 0f;
                    for(int i=0; i<sumsList.size(); i+=2){
                        totalINR += sumsList.get(i);
                    }
                    // Assuming that the first element of sums corresponds to totalSumINR
                    // and the second element corresponds to totalSumUSD
                    for(int i=1; i<sumsList.size(); i+=2){
                        totalUSD += sumsList.get(i);
                    }
                    recordResponse.setTotalINR(totalINR);
                    recordResponse.setTotalUSD(totalUSD);

                    recordResponse.setStatus(HttpStatus.OK);
                    recordResponse.setMessage("Successfully fetched the transactions between the given time range");

                    return ResponseEntity.ok().body(recordResponse);
                });

    }

    /**
     * Converts the price of a given transaction record from its native currency to USD and INR.
     * The conversion is based on the currency exchange rates obtained from an external service/API.
     * If the conversion fails or the necessary exchange rate information is not available,
     * a CurrencyConversionException is thrown.
     *
     * @param record The transaction record containing the original price and currency information.
     * @throws CurrencyConversionException If the currency conversion fails or exchange rate information is not available.
     */
    public void convertCurrency(Record record) {
        try {
            // Extract currency and amount information from the record
            Currency currency = record.getCurrency();
            Float amount = record.getPrice();
            // Retrieve currency exchange rates from the external service using RestTemplate
            // TODO:: RestTemplate is a blocking call so need to change this to WebClient which is suitable for Webflux.
            JsonNode jsonNode = restTemplate.getForObject(currencyConversion.getCurrencyConversionUrl(), JsonNode.class);

            // Ensure that the response from the service is not null
            assert jsonNode != null;

            // Check if Response status is success and exchanges rates for INR is present and throw the exception if any field is missing
            if (Objects.nonNull(jsonNode.get("success")) && (Objects.nonNull(jsonNode.get("rates").get("INR"))) && !jsonNode.get("success").booleanValue() && jsonNode.get("rates").get("INR").floatValue() != 0) {
                throw new CurrencyConversionException("Failed to register transaction as currency conversion failed!");
            }

            // Extract the conversion rate for INR from the response
            Float conversionRate = jsonNode.get("rates").get("INR").floatValue();

            log.info("Successfully fetched the Currency Exchange Rate, exchange rate for 1 USD - Rs. "+conversionRate);

            // Perform currency conversion based on the extracted information
            if (currency.equals(Currency.INR)) {
                // If the native currency is INR, set INR price and calculate USD price
                record.setInrPrice(amount);
                record.setUsdPrice(amount / conversionRate);
                return;
            }
            // If the native currency is not INR, set USD price and calculate INR price
            record.setUsdPrice(amount);
            record.setInrPrice(amount * conversionRate);
        }
        // Catch any uncaught exception then log the exception and throw in form of CurrencyConversionException.
        catch (Exception currencyConversionException){
            log.error("Failed to convert current with the following exception: "+currencyConversionException.getMessage());
            throw new CurrencyConversionException(currencyConversionException.getMessage());
        }
    }
}
