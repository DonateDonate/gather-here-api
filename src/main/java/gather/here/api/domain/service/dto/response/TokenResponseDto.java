package gather.here.api.domain.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TokenResponseDto {
    private String accessToken;
    private String refreshToken;

    public void addPrefix(String accessTokenHeaderName , String refreshTokenHeaderName){
        this.accessToken  = accessTokenHeaderName + " " + accessToken;
        this.refreshToken = refreshTokenHeaderName + " " + refreshToken;
    }
}
