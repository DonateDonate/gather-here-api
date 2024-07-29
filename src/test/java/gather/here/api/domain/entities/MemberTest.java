package gather.here.api.domain.entities;

import gather.here.api.application.dto.request.MemberSignUpRequestDto;
import gather.here.api.infra.exception.BusinessException;
import gather.here.api.infra.exception.MemberException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.support.SimpleTriggerContext;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;


class MemberTest {

    @DisplayName("sut는 새로운 member를 생성한다")
    @Test
    public void testCreate(){
        //arrange
        String id = "01012345678";
        String password = "12341234";
        String encodedPassword = "ENCODEPASSWORDENCODEPASSWORDENC";
        Member actual = null;

        //act
        actual = Member.create(id,password,encodedPassword);

        //assert
        Assertions.assertThat(actual.getId()).isEqualTo(id);
        Assertions.assertThat(actual.getPassword()).isEqualTo(encodedPassword);
    }

    @DisplayName("sut는 id의 길이가 11글자가 아니면 예외가 발생한다")
    @Test
    public void testInvalidId(){
        //arrange
        String id = "1";
        String password = "12312312";
        String encodedPassword = "12312312333333112213";
        BusinessException actual = null;

        //act
        try {
            Member member = Member.create(id, password, encodedPassword);
        } catch (BusinessException e){
            actual = e;
        }


        //assert
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual).isInstanceOf(MemberException.class);
    }

}