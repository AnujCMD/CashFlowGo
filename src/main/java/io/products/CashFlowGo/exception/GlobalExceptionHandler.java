package io.products.CashFlowGo.exception;

import io.products.CashFlowGo.model.response.RecordResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Global exception handler class annotated with {@code @ControllerAdvice}.
 * Extends {@code ResponseEntityExceptionHandler} to provide centralized handling
 * of exceptions thrown in the application.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Exception handler for handling TransactionProcessingException during Transaction processing.
     *
     * @param exception The RuntimeException representing the TransactionProcessingException.
     * @param request   The ServerWebExchange representing the current server web exchange.
     * @return A Mono containing the ResponseEntity<Object> for the handled exception.
     */
    @ExceptionHandler(value = TransactionProcessingException.class)
    protected Mono<ResponseEntity<Object>> transactionProcessingErrorHandler(RuntimeException exception, ServerWebExchange request){
        RecordResponse recordResponse = new RecordResponse();
        recordResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        recordResponse.setMessage("Exception while processing response : " + exception);
        return handleExceptionInternal(exception, recordResponse,
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    /**
     * Exception handler for handling CurrencyConversionException during currency conversion.
     *
     * @param exception The RuntimeException representing the TransactionProcessingException.
     * @param request   The ServerWebExchange representing the current server web exchange.
     * @return A Mono containing the ResponseEntity<Object> for the handled exception.
     */
    @ExceptionHandler(value = CurrencyConversionException.class)
    protected Mono<ResponseEntity<Object>> CurrencyConversionExceptionHandler(RuntimeException exception, ServerWebExchange request){
        RecordResponse recordResponse = new RecordResponse();
        recordResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        recordResponse.setMessage("Exception while processing response : " + exception);
        return handleExceptionInternal(exception, recordResponse,
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(value = NoTransactionsFoundException.class)
    protected Mono<ResponseEntity<Object>> noTransactionFoundException(RuntimeException exception, ServerWebExchange request){
        RecordResponse recordResponse = new RecordResponse();
        recordResponse.setStatus(HttpStatus.NOT_FOUND);
        recordResponse.setMessage("No Transactions Found with Exception : " + exception);
        return handleExceptionInternal(exception, recordResponse,
                new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }
}
