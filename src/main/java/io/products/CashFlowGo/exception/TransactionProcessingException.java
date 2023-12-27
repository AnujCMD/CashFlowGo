package io.products.CashFlowGo.exception;

/**
 * Custom runtime exception class representing an exception during transaction processing.
 * This exception is designed to be thrown when an error occurs during the processing
 * of a transaction, with an optional error message.
 */
public class TransactionProcessingException extends RuntimeException{
    public TransactionProcessingException(String message){
        super(message);
    }
}
