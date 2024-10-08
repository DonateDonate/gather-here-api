package gather.here.api.domain.entities;

import gather.here.api.Utils.Utils;
import gather.here.api.global.exception.BusinessException;
import gather.here.api.global.exception.MemberException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MemberTest {

    @DisplayName("sut는 새로운 member를 생성한다")
    @Test
    public void CreateMemberInstance(){
        //arrange
        String id = Utils.randomMemberId();
        String password = "12341234";
        String encodedPassword = "ENCODEPASSWORDENCODEPASSWORDENC";
        Member actual = null;

        //act
        actual = Member.create(id,encodedPassword);

        //assert
        Assertions.assertThat(actual.getIdentity()).isEqualTo(id);
        Assertions.assertThat(actual.getPassword()).isEqualTo(encodedPassword);
    }

    @DisplayName("sut는 id의 길이가 11글자가 아니면 예외가 발생한다")
    @Test
    public void InvalidId(){
        //arrange
        String id = "1";
        String password = "12312312";
        String encodedPassword = "12312312333333112213";
        BusinessException actual = null;

        //act
        try {
            Member.assertMemberIdentity(id);
        } catch (BusinessException e){
            actual = e;
        }


        //assert
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual).isInstanceOf(MemberException.class);
        Assertions.assertThat(actual.getResponseStatus().getCode()).isEqualTo(1106);
    }
    @DisplayName("sut는 password의 길이가 4글자 이상 8글자 이하가 아니면 예외가 발생한다")
    @Test
    public void InvalidPassword(){
        //arrange
        String id = "1";
        String password = "1";
        BusinessException actual = null;

        //act
        try {
            Member.assertPassword(password);
        } catch (BusinessException e){
            actual = e;
        }


        //assert
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual).isInstanceOf(MemberException.class);
        Assertions.assertThat(actual.getResponseStatus().getCode()).isEqualTo(1107);
    }

}