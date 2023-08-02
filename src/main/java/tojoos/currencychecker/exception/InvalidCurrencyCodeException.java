package tojoos.currencychecker.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidCurrencyCodeException extends RuntimeException {
    private final String errorMessage;

    public InvalidCurrencyCodeException(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
