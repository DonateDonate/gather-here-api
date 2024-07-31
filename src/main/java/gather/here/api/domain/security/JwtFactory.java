package gather.here.api.domain.security;

import gather.here.api.application.dto.response.GetTokenResponseDto;

public interface JwtFactory {
    GetTokenResponseDto generate(String identity, long memberSeq);
    boolean validate(String token);
    String getIdentity(String token);
    //SecurityMemberInfo getAuthentication(String token);
}
