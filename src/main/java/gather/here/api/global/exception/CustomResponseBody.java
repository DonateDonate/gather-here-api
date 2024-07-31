package gather.here.api.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class CustomResponseBody {
    private String result;
    private int code;
}
