package gather.here.api.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class WebSocketAuthException extends BusinessException{
    public WebSocketAuthException(ResponseStatus responseStatus, HttpStatus httpStatus) {
        super(responseStatus, httpStatus);
    }
}
