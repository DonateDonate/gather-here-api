package gather.here.api.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GetTokenResponseDto {
    private String grantType;
    private String accessToken;
    private String refreshToken;
}
