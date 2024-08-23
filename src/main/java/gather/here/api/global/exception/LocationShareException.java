package gather.here.api.global.exception;

import org.springframework.http.HttpStatus;

public class LocationShareException extends BusinessException{
    public LocationShareException(ResponseStatus responseStatus, HttpStatus httpStatus) {
        super(responseStatus, httpStatus);
    }
}
