package io.products.CashFlowGo.exception;

import io.swagger.v3.oas.annotations.Parameter;

public class NoTransactionsFoundException extends RuntimeException{
    public NoTransactionsFoundException(String message){
        super(message);
    }
}
