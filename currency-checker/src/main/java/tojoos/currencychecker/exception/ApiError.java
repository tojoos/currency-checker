package tojoos.currencychecker.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;

public record ApiError(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "hh:mm:ss dd-MM-yyyy")
        LocalDateTime timestamp,
        String path,
        HttpStatus status,
        String errorMessage
) {
}
