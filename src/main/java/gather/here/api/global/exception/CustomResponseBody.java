package gather.here.api.global.exception;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class CustomResponseBody {
    private String result;
    private int code;
    private List<String> errors;

    public CustomResponseBody(String result, int code) {
        this.result = result;
        this.code = code;
    }

    public CustomResponseBody(String result, int code, List<String> errors) {
        this.result = result;
        this.code = code;
        this.errors = errors;
    }
}
