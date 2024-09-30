package gather.here.api.infra.security;

import gather.here.api.domain.security.CryptoFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;

@ActiveProfiles("test")
@SpringBootTest
@WebAppConfiguration
class CryptoFactoryImplTest {

    @Autowired
    private CryptoFactory cryptoFactory;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @DisplayName("sut는 비밀번호를 성공적으로 인코딩한다")
    @Test
    public void successEncodingPassword(){
        //arrange
        CryptoFactory sut = cryptoFactory;
        String password ="12345678";
        String encodingPassword = sut.passwordEncoder(password);

        //act
        Boolean actual = passwordEncoder.matches(password, encodingPassword);

        //assert
        Assertions.assertThat(actual).isTrue();
    }
}