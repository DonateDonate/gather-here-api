package gather.here.api.application.service;

import gather.here.api.application.dto.request.MemberSignUpRequestDto;
import gather.here.api.domain.entities.Member;
import gather.here.api.infra.persistence.jpa.MemberJpaRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    public void saveTest(){
        //arrange
        String id = "01012345678";
        String password = "12341234";
        MemberSignUpRequestDto memberSignUpRequestDto = new MemberSignUpRequestDto(id,password);
        MemberService sut = memberService;

        //act
        sut.save(memberSignUpRequestDto);

        //assert
        Member actual = memberJpaRepository.findById(1L).get();
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getId()).isEqualTo(id);
    }

}