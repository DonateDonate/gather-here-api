package gather.here.api.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler   {

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<Object> handleBusinessException(BusinessException exception) {
        HttpStatus httpStatus = exception.getHttpStatus();
        ResponseStatus responseStatus = exception.getResponseStatus();
        CustomResponseBody customResponseBody = new CustomResponseBody(responseStatus.getMessage(), responseStatus.getCode());
        return new ResponseEntity<>(customResponseBody,httpStatus);
    }

}
