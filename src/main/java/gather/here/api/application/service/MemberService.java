package gather.here.api.application.service;

import gather.here.api.application.dto.request.MemberSignInRequestDto;
import gather.here.api.application.dto.request.MemberSignUpRequestDto;
import gather.here.api.domain.entities.Member;
import gather.here.api.domain.repositories.MemberRepository;
import gather.here.api.domain.security.CryptoFactory;
import gather.here.api.global.exception.MemberException;
import gather.here.api.global.exception.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final CryptoFactory cryptoFactory;

    public void save(MemberSignUpRequestDto request){
        Member originMember = memberRepository.findByIdentity(request.getId());

        if(originMember != null){
            throw new MemberException(ResponseStatus.DUPLICATE_MEMBER_ID, HttpStatus.CONFLICT);
        }

        String encodedPassword = cryptoFactory.passwordEncoder(request.getPassword());
        Member member = Member.create(request.getId(),request.getPassword(),encodedPassword);
        memberRepository.save(member);
    }

    public void signIn(MemberSignInRequestDto request){
        Member originMember = memberRepository.findByIdentity(request.getId());
        Boolean validPassword = cryptoFactory.passwordMatches(request.getPassword(), originMember.getPassword());

        if(!validPassword){
            throw new MemberException(ResponseStatus.INCORRECT_MEMBER_PASSWORD, HttpStatus.UNAUTHORIZED);
        }
    }

}
