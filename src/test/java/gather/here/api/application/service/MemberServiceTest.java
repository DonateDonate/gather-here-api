package gather.here.api.application.service;

import gather.here.api.application.dto.request.MemberSignUpRequestDto;
import gather.here.api.infra.exception.BusinessException;
import gather.here.api.infra.exception.MemberException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class MemberServiceTest {
    @Autowired
    MemberService memberService;

    @Test
    public void saveTest(){
        //arrange
        String id = "010";
        String password = "asd";
        MemberSignUpRequestDto memberSignUpRequestDto = new MemberSignUpRequestDto(id,password);
        BusinessException actual = null;

        //act
        try {
            memberService.save(memberSignUpRequestDto);
        }catch (BusinessException e){
            actual = e;
        }

        //assert
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual).isInstanceOf(BusinessException.class);
    }

}