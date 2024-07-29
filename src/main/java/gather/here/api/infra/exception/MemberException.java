package gather.here.api.infra.exception;

import org.springframework.http.HttpStatus;

public class MemberException extends BusinessException{

    public MemberException(ResponseStatus responseStatus, HttpStatus httpStatus) {
        super(responseStatus, httpStatus);
    }
}
