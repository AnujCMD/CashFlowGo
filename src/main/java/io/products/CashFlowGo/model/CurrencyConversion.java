package io.products.CashFlowGo.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Component class representing configuration properties for currency conversion in the application.
 * It includes the URL used for currency conversion.
 */
@Component
@EnableConfigurationProperties
@ConfigurationProperties("app")
@Data
public class CurrencyConversion {
    private String currencyConversionUrl;
}
