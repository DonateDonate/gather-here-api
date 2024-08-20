package gather.here.api.application.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class LocationShareEventRequestDto {
    private int type;
    private Float presentLat;
    private Float presentLng;
    private Float destinationDistance;

    public LocationShareEventRequestDto(int type, Float presentLat, Float presentLng, Float destinationDistance) {
        this.type = type;
        this.presentLat = presentLat;
        this.presentLng = presentLng;
        this.destinationDistance = destinationDistance;
    }

    @Override
    public String toString() {
        return "{" +
                "type=" + type +
                ", presentLat=" + presentLat +
                ", presentLng=" + presentLng +
                ", destinationDistance=" + destinationDistance +
                '}';
    }
}
