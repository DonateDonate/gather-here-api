package gather.here.api.application.dto.request;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class RoomCreateRequestDto {
    private Float destinationLat;
    private Float destinationLng;
    private String destinationName;
    private String encounterDate;
}
