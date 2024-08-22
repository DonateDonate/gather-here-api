package gather.here.api.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Schema(title = "Room 등록 요청 DTO")
@ToString
@Getter
@AllArgsConstructor
public class RoomCreateRequestDto {

    @NotNull(message = "필수 항목입니다.")
    @Schema(description = "목적지 위도", example = "37.25")
    private Float destinationLat;

    @NotNull(message = "필수 항목입니다.")
    @Schema(description = "목적지 경도", example = "23.4")
    private Float destinationLng;

    @NotNull(message = "필수 항목입니다.")
    @Schema(description = "목적지 이름 (스타벅스 선릉점 또는 서울 강남구 테헤란로 426)", example = "스타벅스 선릉점")
    private String destinationName;

    @NotNull(message = "필수 항목입니다.")
    @Schema(description = "만나는 날짜 (yyyy-MM-dd HH:mm)", example = "2029-01-07 15:33")
    private String encounterDate;
}
