package tojoos.currencychecker.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class UnhandledResponseException extends RuntimeException {
    private final String errorMessage;
    public UnhandledResponseException(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}