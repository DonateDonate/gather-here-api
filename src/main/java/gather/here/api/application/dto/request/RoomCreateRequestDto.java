package gather.here.api.application.dto.request;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RoomCreateRequestDto {
    private Float destinationLat;
    private Float destinationLng;
    private LocalDateTime encounterDate;
}
