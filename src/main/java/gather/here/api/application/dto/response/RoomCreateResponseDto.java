package gather.here.api.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RoomCreateResponseDto {
    private Long roomSeq;
    private Float destinationLat;
    private Float destinationLng;
    private String destinationName;
    private String encounterDate;
    private String shareCode;
}
