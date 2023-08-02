package tojoos.currencychecker.mapper;

import org.springframework.stereotype.Component;
import tojoos.currencychecker.dto.CurrencyRequestDto;
import tojoos.currencychecker.pojo.CurrencyRequest;

@Component
public class CurrencyRequestMapper {
    public CurrencyRequestDto toDto(CurrencyRequest currencyRequest) {
        return new CurrencyRequestDto(
                currencyRequest.getCurrencyCode(),
                currencyRequest.getRequesterName(),
                currencyRequest.getDate(),
                currencyRequest.getCurrencyValue());
    }

    public CurrencyRequest toPojo(CurrencyRequestDto currencyRequestDto) {
        return CurrencyRequest.builder()
                .currencyCode(currencyRequestDto.currency())
                .requesterName(currencyRequestDto.name())
                .date(currencyRequestDto.date())
                .currencyValue(currencyRequestDto.value())
                .build();
    }
}