package gather.here.api.application.service;

import gather.here.api.Utils.Utils;
import gather.here.api.application.dto.request.MemberSignUpRequestDto;
import gather.here.api.domain.entities.Member;
import gather.here.api.global.exception.MemberException;
import gather.here.api.infra.persistence.jpa.MemberJpaRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @DisplayName("sut는 member 인스턴스를 정상적으로 저장한다")
    @Test
    public void saveTest(){
        //arrange
        String id = Utils.randomMemberId();
        String password = "12341234";
        MemberSignUpRequestDto memberSignUpRequestDto = new MemberSignUpRequestDto(id,password);
        MemberService sut = memberService;

        //act
        sut.save(memberSignUpRequestDto);

        //assert
        Member actual = memberJpaRepository.findByIdentity(id);
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
        memberJpaRepository.save(member);

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



}