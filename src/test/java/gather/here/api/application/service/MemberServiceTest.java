package gather.here.api.application.service;

import gather.here.api.Utils.Utils;
import gather.here.api.application.dto.request.MemberSignUpRequestDto;
import gather.here.api.application.dto.request.ModifyNicknameRequestDto;
import gather.here.api.application.dto.request.ModifyPasswordRequestDto;
import gather.here.api.application.dto.response.GetMemberResponseDto;
import gather.here.api.domain.entities.Member;
import gather.here.api.domain.repositories.MemberRepository;
import gather.here.api.global.exception.MemberException;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    EntityManager entityManager;

    @DisplayName("sut는 member 인스턴스를 정상적으로 저장한다")
    @Test
    public void successSaveTest(){
        //arrange
        String id = Utils.randomMemberId();
        String password = "12341234";
        MemberSignUpRequestDto memberSignUpRequestDto = new MemberSignUpRequestDto(id,password);
        MemberService sut = memberService;

        //act
        sut.save(memberSignUpRequestDto);

        //assert
        Member actual = memberRepository.findByIdentity(id).get();
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getIdentity()).isEqualTo(id);
    }

    @DisplayName("sut는 저장중 중복된 identity가 존재하면 예외가 발생한다")
    @Test
    public void saveDuplicateTest(){
        //arrange
        MemberService sut = memberService;
        String id =Utils.randomMemberId();
        String password = "12341234";

        Member member = Member.create(id, password, password);
        memberRepository.save(member);

        MemberSignUpRequestDto memberSignUpRequestDto = new MemberSignUpRequestDto(id,password);
        MemberException actual = null;

        //act
        try {
            sut.save(memberSignUpRequestDto);
        }catch (MemberException e){
            actual = e;
        }

        //assert
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual).isInstanceOf(MemberException.class);
        Assertions.assertThat(actual.getResponseStatus().getCode()).isEqualTo(1101);
    }

    @DisplayName("sut는 성공적으로 member 정보를 가져온다")
    @Test
    public void successGetMemberTest(){
        //arrange
        String id = createMember();
        MemberService sut = memberService;
        Member member = memberRepository.findByIdentity(id).get();


        //act
        GetMemberResponseDto response = sut.getMember(member.getSeq());

        //assert
        Assertions.assertThat(response.getIdentity()).isEqualTo(id);
    }

    @DisplayName("sut는 member의 imagekey가 null이 아니면 imageUrl를 포함시켜 반환한다")
    @Test
    public void getMemberContainImageUrlTest(){
        //arrange
        String id = createMember();
        MemberService sut = memberService;
        Member member = memberRepository.findByIdentity(id).get();
        member.setImageKey(String.valueOf(UUID.randomUUID()));

        //act
        GetMemberResponseDto response = sut.getMember(member.getSeq());

        //assert
        Assertions.assertThat(response.getProfileImageUrl()).isNotNull();
    }

    @DisplayName("sut는 성공적으로 닉네임을 변경한다")
    @Test
    public void successModifyNicknameTest(){
        //arrange
        MemberService sut = memberService;
        String memberIdentity = createMember();
        Member member = memberRepository.findByIdentity(memberIdentity).get();
        String newNickname = "spring";
        ModifyNicknameRequestDto request = new ModifyNicknameRequestDto(newNickname);
        //act
        sut.modifyNickname(request,member.getSeq());
        Member modifyNicknameMember = memberRepository.findByIdentity(memberIdentity).get();

        //assert
        Assertions.assertThat(modifyNicknameMember.getSeq()).isEqualTo(member.getSeq());
        Assertions.assertThat(modifyNicknameMember.getNickname()).isEqualTo(newNickname);
    }

    @DisplayName("sut는 성공적으로 비밀번호를 변경한다")
    @Test
    public void successModifyPassword(){
        //arrange
        MemberService sut = memberService;
        String memberIdentity = createMember();
        Member member = memberRepository.findByIdentity(memberIdentity).get();
        String newPassword = "12341";
        ModifyPasswordRequestDto request = new ModifyPasswordRequestDto(newPassword);

        //act
        sut.modifyPassword(request,member.getSeq());
        Member modifyPasswordMember = memberRepository.findByIdentity(memberIdentity).get();
        //assert
        Assertions.assertThat(
                passwordEncoder.matches(newPassword, modifyPasswordMember.getPassword())).isTrue();
    }

    @DisplayName("sut는 성공적으로 member의 isActive를 false로 변경한다")
    @Test
    public void successCancelAccountTest(){
        //arrange
        String member2 = createMember();
        MemberService sut = memberService;
        String memberIdentity = createMember();
        Member member = memberRepository.findByIdentity(memberIdentity).get();

        //act
        sut.cancelAccount(member.getSeq());

        //assert
        Assertions.assertThat(member.isActive()).isFalse();
    }

    private String createMember() {
        String id = Utils.randomMemberId();
        String password = "12341234";
        MemberSignUpRequestDto memberSignUpRequestDto = new MemberSignUpRequestDto(id,password);
        memberService.save(memberSignUpRequestDto);
        return id;
    }





}