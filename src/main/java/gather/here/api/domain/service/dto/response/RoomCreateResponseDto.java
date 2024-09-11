package gather.here.api.domain.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RoomCreateResponseDto {
    @Schema(description = "Room 식별값", example = "1")
    private Long roomSeq;

    @Schema(description = "목적지 위도", example = "37.25")
    private Double destinationLat;

    @Schema(description = "목적지 경도", example = "23.4")
    private Double destinationLng;

    @Schema(description = "목적지 이름 (스타벅스 선릉점 또는 서울 강남구 테헤란로 426)", example = "스타벅스 선릉점")
    private String destinationName;

    @Schema(description = "만나는 날짜 (yyyy-MM-dd HH:mm)", example = "2029-01-07 15:33")
    private String encounterDate;

    @Schema(description = "공유 코드(4자리 숫자,영어)", example = "AB14")
    private String shareCode;
}
