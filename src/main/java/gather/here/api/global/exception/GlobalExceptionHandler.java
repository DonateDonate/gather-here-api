package gather.here.api.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler   {


    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<Object> handleBusinessException(BusinessException e) {
        e.printStackTrace();
        log.error("[BusinessException] code: {}, message: {}",e.getResponseStatus().getCode(),e.getResponseStatus().getMessage());
        HttpStatus httpStatus = e.getHttpStatus();
        ResponseStatus responseStatus = e.getResponseStatus();
        CustomResponseBody customResponseBody = new CustomResponseBody(responseStatus.getMessage(), responseStatus.getCode());
        return new ResponseEntity<>(customResponseBody,httpStatus);
    }

    @ResponseBody
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("[HttpMessageNotReadableException] cause: {}, message: {}", NestedExceptionUtils.getMostSpecificCause(e),e.getMessage());
        CustomResponseBody customResponseBody = new CustomResponseBody(ResponseStatus.INVALID_REQUEST.getMessage(), ResponseStatus.INVALID_REQUEST.getCode());
        return new ResponseEntity<>(customResponseBody, // body
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        log.error("[MethodArgumentNotValidException] cause: {}, message: {}", NestedExceptionUtils.getMostSpecificCause(e),e.getMessage());
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        List<String> errors = new ArrayList<>();
        for(FieldError fieldError : fieldErrors){
            errors.add(fieldError.getField() + " " + fieldError.getDefaultMessage());
        }
        CustomResponseBody customResponseBody = new CustomResponseBody(
                ResponseStatus.INVALID_REQUEST.getMessage(),
                ResponseStatus.INVALID_REQUEST.getCode(),
                errors);

        return new ResponseEntity<>(customResponseBody, // body
                HttpStatus.BAD_REQUEST);
    }

    //모든 에러 -> 하위 에러에서 못받을 때
    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception e){
        log.error("[Exception] cause: {} , message: {}", NestedExceptionUtils.getMostSpecificCause(e), e.getMessage());
        CustomResponseBody customResponseBody = new CustomResponseBody(ResponseStatus.INTERNAL_SERVER_ERROR.getMessage(), ResponseStatus.INTERNAL_SERVER_ERROR.getCode());
        return new ResponseEntity<>(customResponseBody, // body
                HttpStatus.INTERNAL_SERVER_ERROR);
    }



}
