package gather.here.api.global.exception;

import lombok.Getter;

@Getter
public enum ResponseStatus {


    /**
     *  1xxx : member Exception
     */
    DUPLICATE_MEMBER_ID(1101,"중복된 아이디입니다."),
    UNCORRECTED_MEMBER_NICKNAME(1103, "잘못된 요청입니다. nickname 값을 확인해주세요"),
    NOT_FOUND_MEMBER(1105,"등록되지 않은 member식별 값입니다"),
    INCORRECT_IDENTITY(1106, "잘못된 요청입니다. identity 값을 확인해주세요"),
    INCORRECT_PASSWORD(1107, "잘못된 요청입니다. password 값을 확인해주세요"),
    INCORRECT_IMAGE_KEY(1108, "잘못된 요청입니다. imageKey 값을 확인해주세요"),


    /**
     * 2xxx: room exception
     */
    NOT_FOUND_SHARE_CODE(2101,"등록되지 않은 공유코드입니다"),
    INCORRECT_ENCOUNTER_DATE(2103,"잘못된 요청입니다. encounterDate 값을 확인해주세요"),
    ALREADY_ROOM_ENCOUNTER(2104,"이미 참가한 방이 있습니다"),
    CLOSED_ROOM(2105,"종료된 방입니다"),
    NOT_FOUND_ROOM_SEQ(2106,"등록되지 않은 room식별값입니다"),
    PAST_ENCOUNTER_DATE(2107,"잘못된 요청입니다. encounterDate 값을 확인해주세요(과거)"),
    IS_MORE_THAN_24_HOURS_ENCOUNTER_DATE(2108,"잘못된 요청입니다. encounterDate 값을 확인해주세요(24시간 지남)"),

    /**
     * 3xxx: Location Share exception
     */
    DUPLICATE_WEB_SOCKET_AUTH_MEMBER_SEQ(3102,"이미 connect 완료한 member입니다."),
    DUPLICATE_LOCATION_SHARE_EVENT_ROOM_SEQ(3103,"이미 locationShareEvent에 참가한 member입니다."),
    NOT_FOUND_LOCATION_SHARE_EVENT(3104,"해당 room은 locationShareEvent가 없습니다"),
    NOT_FOUND_SESSION_ID(3105,"등록되지 않은 sessionId입니다"),
    ALREADY_ARRIVED_MEMBER(3106,"이미 목적지에 도착한 member입니다"),

    /**
     *  8xxx : general exception
     */

    INVALID_REQUEST(8101, "잘못된 요청입니다"),
    INTERNAL_SERVER_ERROR(8999,"내부 서버문제로 작업이 실패했습니다. 잠시 후 다시 시도해주세요"),

    /**
     *  9xxx : auth exception
     */

    INVALID_ACCESS_TOKEN(9102,"access token 값을 다시 확인해주세요"),
    EXPIRE_ACCESS_TOKEN(9103,"만료된 access token입니다"),
    EMPTY_ACCESS_TOKEN(9104,"access token이 없습니다"),
    INVALID_REFRESH_TOKEN(9105,"refresh token 값을 다시 확인해주세요"),
    EXPIRE_REFRESH_TOKEN(9106,"만료된 refresh token입니다"),
    TOKEN_TYPE_ERROR(9107, "요청하신 token prefix 값을 다시 확인해주세요"),
    INCORRECT_ACCOUNT(9108, "잘못된 요청입니다. identity 및 password 값을 확인해주세요");

    private int code;
    private String message;

    ResponseStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
