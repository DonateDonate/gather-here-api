package gather.here.api.global.exception;

import lombok.Getter;

@Getter
public enum ResponseStatus {


    /**
     *  1xxx : member Exception
     */
    DUPLICATE_MEMBER_ID(1101,"duplicated member id"),
    INCORRECT_MEMBER_PASSWORD(1102, "incorrected member password"),


    /**
     *  8xxx : general exception
     */

    INVALID_INPUT(8101, "Invalid Input"),

    /**
     *  9xxx : auth exception
     */

    NOT_AUTH(9101, "asd");



    private int code;
    private String message;

    ResponseStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
