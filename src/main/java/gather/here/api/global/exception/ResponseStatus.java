package gather.here.api.global.exception;

import lombok.Getter;

@Getter
public enum ResponseStatus {


    /**
     *  1xxx : member Exception
     */
    DUPLICATE_MEMBER_ID(1101,"duplicated member id"),
    UNCORRECTED_MEMBER_ID_PASSWORD(1102, "uncorrected member id or password"),

    /**
     * 2xxx: room exception
     */
    NOT_FOUND_SHARE_CODE(2101,"not found share code"),


    /**
     *  8xxx : general exception
     */

    INVALID_INPUT(8101, "Invalid Input"),
    NOT_FOUND(8102,"not found resource"),

    /**
     *  9xxx : auth exception
     */

    INVALID_IDENTITY_PASSWORD(9101, "invalid identity or password"),
    INVALID_TOKEN(9102,"invalid token"),
    EXPIRE_TOKEN(9103,"expire token"),
    EMPTY_TOKEN(9104,"empty token");



    private int code;
    private String message;

    ResponseStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
