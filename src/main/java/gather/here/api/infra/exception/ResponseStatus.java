package gather.here.api.infra.exception;

import lombok.Getter;

@Getter
public enum ResponseStatus {

    /**
     *  1xxx : general exception
     */

    INVALID_INPUT(1101, "Invalid Input");

    private int code;
    private String message;

    ResponseStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
