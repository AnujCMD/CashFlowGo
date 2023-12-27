package io.products.CashFlowGo.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.products.CashFlowGo.model.request.Record;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;
@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class RecordResponse {
    private List<Record> recordList;
    private HttpStatus status;
    private String message;
}
