package gather.here.api.application.dto.request;

import lombok.Getter;

@Getter
public class LocationShareEventRequestDto {
    private Float presentLat;
    private Float presentLng;
    private Float destinationDistance;
}
