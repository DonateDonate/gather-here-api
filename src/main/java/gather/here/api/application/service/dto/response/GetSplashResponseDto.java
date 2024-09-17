package gather.here.api.application.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GetSplashResponseDto {
    @Schema(description = "app 버전정보", example = "1.1.0")
    private String appVersion;

    @Schema(description = "app location status 정보 0 : 아무 이벤트 없음 / 1 : 위치공유 / ")
    private int status;
}
