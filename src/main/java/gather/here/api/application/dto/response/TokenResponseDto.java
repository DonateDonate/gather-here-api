package gather.here.api.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TokenResponseDto {
    private String accessToken;
    private String refreshToken;
}
