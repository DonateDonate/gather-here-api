package gather.here.api.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException{
    private ResponseStatus responseStatus;
    private HttpStatus httpStatus;

    public BusinessException(ResponseStatus responseStatus, HttpStatus httpStatus) {
        this.responseStatus = responseStatus;
        this.httpStatus = httpStatus;
    }
}
