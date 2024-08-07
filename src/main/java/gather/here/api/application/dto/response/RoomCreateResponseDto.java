package gather.here.api.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class RoomCreateResponseDto {
    private Long roomSeq;
    private Float destinationLat;
    private Float destinationLng;
    private LocalDateTime encounterDate;
}
