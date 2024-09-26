package gather.here.api.domain.service;

import gather.here.api.domain.service.dto.request.MemberSignUpRequestDto;
import gather.here.api.domain.service.dto.request.ModifyNicknameRequestDto;
import gather.here.api.domain.service.dto.request.ModifyPasswordRequestDto;
import gather.here.api.domain.service.dto.response.GetMemberResponseDto;
import gather.here.api.domain.service.dto.response.UpdateImageResponseDto;
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

import static gather.here.api.domain.entities.Member.assertMemberIdentity;
import static gather.here.api.domain.entities.Member.assertPassword;


@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final CryptoFactory cryptoFactory;
    private final FileFactory fileFactory;

    @Transactional
    public void save(MemberSignUpRequestDto request) {
        boolean isExistMember = memberRepository.findByIdentityAndIsActiveTrue(request.getIdentity())
                .isPresent();
        assertRequest(request, isExistMember);

        String encodedPassword = cryptoFactory.passwordEncoder(request.getPassword());
        Member member = Member.create(request.getIdentity(), encodedPassword);
        memberRepository.save(member);
    }

    private static void assertRequest(MemberSignUpRequestDto request, boolean isExistMember) {
        if (isExistMember) {
            throw new MemberException(ResponseStatus.DUPLICATE_MEMBER_ID, HttpStatus.FORBIDDEN);
        }
        assertPassword(request.getPassword());
        assertMemberIdentity(request.getIdentity());
    }

    public GetMemberResponseDto getMember(Long memberSeq) {
        Member member = memberRepository.getBySeq(memberSeq);
        String imageUrl = StringUtils.isNotEmpty(member.getImageKey()) ? fileFactory.getImageUrl(member.getImageKey()) : null;

        return new GetMemberResponseDto(member.getNickname(), member.getIdentity(), imageUrl);
    }

    @Transactional
    public void modifyNickname(ModifyNicknameRequestDto request, Long memberSeq) {
        Member member = memberRepository.getBySeq(memberSeq);
        member.setNickname(request.getNickname());
    }

    @Transactional
    public void modifyPassword(ModifyPasswordRequestDto request, Long memberSeq) {
        Member member = memberRepository.getBySeq(memberSeq);
        String encodedPassword = cryptoFactory.passwordEncoder(request.getPassword());
        member.modifyPassword(request.getPassword(),encodedPassword);
    }

    @Transactional
    public void cancelAccount(Long memberSeq){
        Member member = memberRepository.getBySeq(memberSeq);
        member.cancelAccount();
    }

    @Transactional
    public UpdateImageResponseDto updateMemberImage(MultipartFile multipartFile, Long memberSeq) {
        String imageKey = fileFactory.uploadFile(multipartFile);
        Member member = memberRepository.getBySeq(memberSeq);
        if(member.getImageKey() != null){
            fileFactory.deleteFile(member.getImageKey());
        }
        member.setImageKey(imageKey);
        return new UpdateImageResponseDto(fileFactory.getImageUrl(imageKey));
    }

    public boolean isJoinRoom(Long memberSeq) {
        Member member = memberRepository.getBySeq(memberSeq);
        if (member.getRoom() != null && member.getRoom().getStatus() == 1) {
            return true;
        }
        return false;
    }
}
