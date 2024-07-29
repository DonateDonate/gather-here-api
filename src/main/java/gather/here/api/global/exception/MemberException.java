package gather.here.api.global.exception;

import org.springframework.http.HttpStatus;

public class MemberException extends BusinessException{

    public MemberException(ResponseStatus responseStatus, HttpStatus httpStatus) {
        super(responseStatus, httpStatus);
    }
}
