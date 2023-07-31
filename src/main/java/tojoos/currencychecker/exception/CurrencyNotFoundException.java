package tojoos.currencychecker.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.NOT_FOUND)
public class CurrencyNotFoundException extends RuntimeException {
    private final String errorMessage;

    public CurrencyNotFoundException(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
