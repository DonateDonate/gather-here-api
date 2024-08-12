package gather.here.api.application.service;

import gather.here.api.application.dto.request.MemberSignUpRequestDto;
import gather.here.api.domain.entities.Member;
import gather.here.api.domain.repositories.MemberRepository;
import gather.here.api.domain.security.CryptoFactory;
import gather.here.api.global.exception.MemberException;
import gather.here.api.global.exception.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final CryptoFactory cryptoFactory;

    @Transactional
    public void save(MemberSignUpRequestDto request){
        boolean isExistMember = memberRepository.findByIdentity(request.getIdentity()).isPresent();

        if(isExistMember){
            throw new MemberException(ResponseStatus.DUPLICATE_MEMBER_ID, HttpStatus.CONFLICT);
        }
        String encodedPassword = cryptoFactory.passwordEncoder(request.getPassword());
        Member member = Member.create(request.getIdentity(),request.getPassword(),encodedPassword);
        memberRepository.save(member);
    }

    //정보 조회 1순위


    //프로필 이미지 수정
    //닉네임수정
    //프로필 이미지 수정
    //비밀번호 변경
    //회원탈퇴
}
