package gather.here.api.global.exception;

import org.springframework.http.HttpStatus;

public class TokenExpiredException extends AuthException{
    public TokenExpiredException(ResponseStatus responseStatus, HttpStatus httpStatus) {
        super(responseStatus, httpStatus);
    }
}
