package io.products.CashFlowGo.exception;

/**
 * Custom runtime exception class representing an exception during Currency Conversion.
 * This exception is designed to be thrown when an error occurs during the conversion
 * of currency, with an optional error message.
 */
public class CurrencyConversionException extends RuntimeException{
    public CurrencyConversionException(String message){
        super(message);
    }
}
