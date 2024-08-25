package gather.here.api.global.exception;

import lombok.Getter;

@Getter
public enum ResponseStatus {


    /**
     *  1xxx : member Exception
     */
    DUPLICATE_MEMBER_ID(1101,"duplicated member id"),
    UNCORRECTED_MEMBER_ID_PASSWORD(1102, "uncorrected member id or password"),
    UNCORRECTED_MEMBER_NICKNAME(1103, "uncorrected member nickname"),
    UNCORRECTED_MEMBER_SEQ(1104,"uncorrected member seq"),

    /**
     * 2xxx: room exception
     */
    NOT_FOUND_SHARE_CODE(2101,"not found share code"),
    PAST_DATE_INVALID(2102,"encounter date cannot be in the past"),
    ENCOUNTER_DATE_INVALID(2103,"encounter date invalid format"),
    ALREADY_ROOM_ENCOUNTER(2104,"already room encounter"),
    CLOSED_ROOM(2105,"this is closed room"),
    NOT_FOUND_ROOM_SEQ(2106,"not found room seq"),

    /**
     * 3xxx: Location Share exception
     */
    DUPLICATE_SESSION_ID(3101,"duplicate session id"),
    DUPLICATE_WEB_SOCKET_AUTH_MEMBER_SEQ(3102,"duplicate webSocketAuth memberSeq"),
    DUPLICATE_LOCATION_SHARE_EVENT_ROOM_SEQ(3103,"duplicate share event roomSeq"),
    NOT_FOUND_LOCATION_SHARE_EVENT_BY_ROOM_SEQ(3104,"not found shareEvent by roomSeq"),
    NOT_FOUND_SESSION_ID(3105,"not found session id"),

    /**
     *  8xxx : general exception
     */

    INVALID_INPUT(8101, "Invalid Input"),
    NOT_FOUND(8102,"not found resource"),
    INTERNAL_SERVER_ERROR(9999,"internal server error"),


    /**
     *  9xxx : auth exception
     */

    INVALID_IDENTITY_PASSWORD(9101, "invalid identity or password"),
    INVALID_ACCESS_TOKEN(9102,"invalid access token"),
    EXPIRE_ACCESS_TOKEN(9103,"expire access token"),
    EMPTY_ACCESS_TOKEN(9104,"empty access token"),
    INVALID_REFRESH_TOKEN(9105,"invalid refresh token"),
    EXPIRE_REFRESH_TOKEN(9106,"expire refresh token"),
    TOKEN_TYPE_ERROR(9107, "invalid token type error");




    private int code;
    private String message;

    ResponseStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
