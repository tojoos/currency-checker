package tojoos.currencychecker.dto;

import java.time.LocalDateTime;

public record CurrencyRequestDto(
        String currency,
        String name,
        LocalDateTime date,
        Double value
) {
}
