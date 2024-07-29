package gather.here.api.application.service;

import gather.here.api.application.dto.request.MemberSignUpRequestDto;
import gather.here.api.domain.entities.Member;
import gather.here.api.domain.repositories.MemberRepository;
import gather.here.api.infra.exception.MemberException;
import gather.here.api.infra.exception.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void save(MemberSignUpRequestDto request){

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        Member member = Member.create(request.getId(),request.getPassword(),encodedPassword);
        memberRepository.save(member);
    }

    public void signIn(){

    }

}
