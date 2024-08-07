package gather.here.api.global.exception;

import org.springframework.http.HttpStatus;

public class RoomException extends BusinessException{
    public RoomException(ResponseStatus responseStatus, HttpStatus httpStatus) {
        super(responseStatus, httpStatus);
    }
}
