package gather.here.api.application.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class GetLocationShareResponseDto {
    private LocationShareMessage locationShareMessage;
    private List<String> sessionIdList;

    public GetLocationShareResponseDto(LocationShareMessage locationShareMessage, List<String> sessionIdList) {
        this.locationShareMessage = locationShareMessage;
        this.sessionIdList = sessionIdList;
    }
}
