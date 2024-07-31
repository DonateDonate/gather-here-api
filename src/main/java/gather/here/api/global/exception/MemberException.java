package gather.here.api.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

@Slf4j
public class MemberException extends BusinessException{
    public MemberException(ResponseStatus responseStatus, HttpStatus httpStatus) {
        super(responseStatus, httpStatus);
    }
}
