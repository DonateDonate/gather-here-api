package gather.here.api.application.service;

import gather.here.api.application.dto.request.MemberSignUpRequestDto;
import gather.here.api.application.dto.request.ModifyNicknameRequestDto;
import gather.here.api.application.dto.request.ModifyPasswordRequestDto;
import gather.here.api.application.dto.response.GetMemberResponseDto;
import gather.here.api.application.dto.response.UpdateImageResponseDto;
import gather.here.api.domain.entities.Member;
import gather.here.api.domain.file.FileFactory;
import gather.here.api.domain.repositories.MemberRepository;
import gather.here.api.domain.security.CryptoFactory;
import gather.here.api.global.exception.MemberException;
import gather.here.api.global.exception.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final CryptoFactory cryptoFactory;
    private final FileFactory fileFactory;

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

    public GetMemberResponseDto getMember(Long memberSeq){
        Member member = findByMemberSeq(memberSeq);
        String imageUrl = StringUtils.isNotEmpty(member.getImageKey()) ? fileFactory.getImageUrl(member.getImageKey()) : null;

        return new GetMemberResponseDto(member.getNickname(), member.getIdentity(), imageUrl);
    }

    @Transactional
    public void modifyNickname(ModifyNicknameRequestDto request, Long memberSeq){
        Member member = findByMemberSeq(memberSeq);
        member.setNickname(request.getNickname());
    }

    @Transactional
    public void modifyPassword(ModifyPasswordRequestDto request, Long memberSeq){
        Member member = findByMemberSeq(memberSeq);
        String encodedPassword = cryptoFactory.passwordEncoder(request.getPassword());
        member.modifyPassword(request.getPassword(),encodedPassword);
    }

    @Transactional
    public void cancelAccount(Long memberSeq){
        Member member = findByMemberSeq(memberSeq);
        member.cancelAccount();
    }

    @Transactional
    public UpdateImageResponseDto updateMemberImage(MultipartFile multipartFile, Long memberSeq){
        Member member = findByMemberSeq(memberSeq);
        if(member.getImageKey() != null){
            fileFactory.deleteFile(member.getImageKey());
        }
        String imageKey = fileFactory.uploadFile(multipartFile);

        member.setImageKey(imageKey);
        return new UpdateImageResponseDto(fileFactory.getImageUrl(imageKey));
    }

    private Member findByMemberSeq(Long memberSeq) {
       return memberRepository.findBySeq(memberSeq).orElseThrow(
                ()-> new MemberException(ResponseStatus.NOT_FOUND_MEMBER,HttpStatus.BAD_REQUEST)
        );
    }
}
