package gather.here.api.application.service;

import gather.here.api.application.dto.request.MemberSignUpRequestDto;
import gather.here.api.application.dto.request.ModifyNicknameRequestDto;
import gather.here.api.application.dto.request.ModifyPasswordRequestDto;
import gather.here.api.application.dto.response.GetMemberResponseDto;
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
    private final FileService fileService;

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


    public GetMemberResponseDto getMember(String memberIdentity){
        Member member = memberRepository.findByIdentity(memberIdentity).orElseThrow(
                ()-> new MemberException(ResponseStatus.INVALID_IDENTITY_PASSWORD,HttpStatus.BAD_REQUEST)
        );
        String profileImageUrl = fileService.getProfileImageUrl(member.getImageKey());
        return new GetMemberResponseDto(member.getNickname(), member.getIdentity(), profileImageUrl);
    }

    @Transactional
    public void modifyNickname(ModifyNicknameRequestDto request, String memberIdentity){
        Member member = memberRepository.findByIdentity(memberIdentity).orElseThrow(
                ()-> new MemberException(ResponseStatus.INVALID_IDENTITY_PASSWORD,HttpStatus.BAD_REQUEST)
        );
        member.setNickname(request.getNickname());
    }

    @Transactional
    public void modifyPassword(ModifyPasswordRequestDto request, String memberIdentity){
        Member member = memberRepository.findByIdentity(memberIdentity).orElseThrow(
                ()-> new MemberException(ResponseStatus.INVALID_IDENTITY_PASSWORD,HttpStatus.BAD_REQUEST)
        );
        member.setPassword(request.getPassword());
    }

    @Transactional
    public void cancelAccount(String memberIdentity){
        Member member = memberRepository.findByIdentity(memberIdentity).orElseThrow(
                ()-> new MemberException(ResponseStatus.INVALID_IDENTITY_PASSWORD,HttpStatus.BAD_REQUEST)
        );
        member.cancelAccount();
    }
}
//프로필 이미지 수정
