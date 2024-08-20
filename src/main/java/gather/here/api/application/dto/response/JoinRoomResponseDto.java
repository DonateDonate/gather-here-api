package gather.here.api.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class JoinRoomResponseDto {
    @Schema(description = "Room 식별값", example = "1")
    private Long roomSeq;

    @Schema(description = "목적지 위도", example = "37.25")
    private Float destinationLat;

    @Schema(description = "목적지 경도", example = "23.4")
    private Float destinationLng;

    @Schema(description = "목적이 이름 (ex) 스타벅스 선릉역점 없으면 주소)", example = "스타벅스 선릉역점")
    private String destinationName;

    @Schema(description = "만나는 날짜 (yyyy-MM-dd HH:mm)", example = "2029-01-07 15:33")
    private String encounterDate;

    @Schema(description = "공유 코드(4자리 숫자,영어)", example = "AB14")
    private String shareCode;
}
