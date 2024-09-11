package gather.here.api.domain.service.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class LocationShareEventRequestDto {
    private int type;
    private Double presentLat;
    private Double presentLng;
    private Double destinationDistance;

    public LocationShareEventRequestDto(int type, Double presentLat, Double presentLng, Double destinationDistance) {
        this.type = type;
        this.presentLat = presentLat;
        this.presentLng = presentLng;
        this.destinationDistance = destinationDistance;
    }
}
