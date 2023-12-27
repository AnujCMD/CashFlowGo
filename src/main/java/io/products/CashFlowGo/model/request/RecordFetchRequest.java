package io.products.CashFlowGo.model.request;

import io.products.CashFlowGo.model.request.enums.TimeStampType;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Request object for fetching transaction records with specified timestamp details.
 * It includes parameters for specifying the timestamp range, timestamp type, and
 * the current timestamp for reference.
 */
@Data
public class RecordFetchRequest {
    @Schema(description = "The epoch of FROM range if CUSTOM timestamp type is selected")
    private Long epochFrom;
    @Schema(description = "The epoch of To range if CUSTOM timestamp type is selected")
    private Long epochTo;
    @Schema(description = "The quick filter to fetch the transactions or select CUSTOM to enter custom range in epoch")
    private TimeStampType timeStampType;
    @Schema(hidden = true)
    private LocalDateTime currentTimeStamp;
}
