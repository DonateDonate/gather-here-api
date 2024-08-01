//package gather.here.api.infra.security;
//
//import gather.here.api.application.dto.response.GetTokenResponseDto;
//import gather.here.api.domain.security.JwtFactory;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@ActiveProfiles("test")
//class JwtFactoryImplTest {
//
//    @Autowired
//    JwtFactory jwtFactory;
//
//    @Test
//    @DisplayName("sut는 토큰을 생성한다")
//    public void getTokenTest(){
//        //arrange
//        String identity = "01023456678";
//        long memberSeq = 1L;
//        JwtFactory sut = jwtFactory;
//
//        //act
//        GetTokenResponseDto actual = sut.generate(identity);
//
//        //assert
//        Assertions.assertThat(actual).isNotNull();
//    }
//
//    @Test
//    @DisplayName("sut는 유효한 토큰을 생성한다")
//    public void tokenValidTest(){
//        //arrange
//        String identity = "01023456678";
//        long memberSeq = 1L;
//        JwtFactory sut = jwtFactory;
//        GetTokenResponseDto getTokenResponseDto = sut.generate(identity);
//
//        //act
//        boolean isValidAccessToken = sut.validate(getTokenResponseDto.getAccessToken());
//        boolean isValidRefreshToken = sut.validate(getTokenResponseDto.getRefreshToken());
//
//
//        //assert
//        Assertions.assertThat(isValidAccessToken).isTrue();
//        Assertions.assertThat(isValidRefreshToken).isTrue();
//    }
//
//    @Test
//    @DisplayName("sut는 유효하지 않은 토큰을 검증한다")
//    public void tokenInValidTest(){
//        //arrange
//        String identity = "01023456678";
//        long memberSeq = 1L;
//        JwtFactory sut = jwtFactory;
//        GetTokenResponseDto getTokenResponseDto = new GetTokenResponseDto("Bearer", "invalid access token", "invalid refresh token");
//
//        //act
//        boolean isValidAccessToken = sut.validate(getTokenResponseDto.getAccessToken());
//        boolean isValidRefreshToken = sut.validate(getTokenResponseDto.getRefreshToken());
//
//
//        //assert
//        Assertions.assertThat(isValidAccessToken).isFalse();
//        Assertions.assertThat(isValidRefreshToken).isFalse();
//    }
//
//
//
//
//}