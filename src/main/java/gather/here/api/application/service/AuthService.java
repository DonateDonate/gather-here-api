package gather.here.api.application.service;

import gather.here.api.application.dto.request.MemberSignInRequestDto;
import gather.here.api.application.dto.response.GetTokenResponseDto;
import gather.here.api.domain.entities.Member;
import gather.here.api.domain.entities.Token;
import gather.here.api.domain.repositories.MemberRepository;
import gather.here.api.domain.repositories.TokenRepository;
import gather.here.api.domain.security.CryptoFactory;
import gather.here.api.domain.security.JwtFactory;
import gather.here.api.global.exception.MemberException;
import gather.here.api.global.exception.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;
    private final CryptoFactory cryptoFactory;
    private final JwtFactory jwtFactory;

    public GetTokenResponseDto getToken(MemberSignInRequestDto request){
        Member member = memberRepository.findByIdentity(request.getId())
                .orElseThrow(() -> new MemberException(ResponseStatus.NOT_FOUND, HttpStatus.NOT_FOUND));

        Boolean validPassword = cryptoFactory.passwordMatches(request.getPassword(), member.getPassword());

        if(!validPassword){
            throw new MemberException(ResponseStatus.UNCORRECTED_MEMBER_PASSWORD, HttpStatus.UNAUTHORIZED);
        }

        GetTokenResponseDto tokenRes = jwtFactory.generate(member.getIdentity(), member.getSeq());
        Token token = Token.create(member.getSeq(), tokenRes.getRefreshToken());
        tokenRepository.save(token);

        return tokenRes;
    }
}
