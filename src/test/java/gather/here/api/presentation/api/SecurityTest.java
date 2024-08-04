package gather.here.api.presentation.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import gather.here.api.application.dto.request.MemberSignInRequestDto;
import gather.here.api.application.dto.request.MemberSignUpRequestDto;
import gather.here.api.application.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SecurityTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MemberService memberService;

    @Test
    @DisplayName("sut는 정상적으로 회원가입 요청을 받는다")
    void signUpTest() throws Exception {
        //arrange
        MemberSignUpRequestDto requestDto = new MemberSignUpRequestDto("01012345678","12341234");
        String url = "http://localhost:" + port + "/members";
        //act

        ResponseEntity<UUID> actual = restTemplate.postForEntity(url,requestDto, UUID.class);

        //assert
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("sut는 로그인을 하고 token을 header로 응답받는다")
    public void loginTest(){
        //arrange
        String identity = "01012345678";
        String password = "12341234";
        memberSave(identity, password);

        MemberSignInRequestDto signInRequestDto = new MemberSignInRequestDto(identity,password);

        String url = "http://localhost:" + port + "/login";


        //act
        ResponseEntity<UUID> actual = restTemplate.postForEntity(url,signInRequestDto, UUID.class);

        //assert
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getHeaders().get("Authorization").get(0)).isNotNull();
        assertThat(actual.getHeaders().get("Refresh-token").get(0)).isNotNull();
    }

//    @Test
//    @DisplayName("sut는 access-token으로 인증을 받는다")
//    public void test(){
//
//        //arrange
//        String identity = "01012345678";
//        String password = "12341234";
//        memberSave(identity, password);
//
//        MemberSignInRequestDto signInRequestDto = new MemberSignInRequestDto(identity,password);
//
//        String loginUrl = "http://localhost:" + port + "/login";
//        ResponseEntity<UUID> loginRequest = restTemplate.postForEntity(loginUrl,signInRequestDto, UUID.class);
//        String accessToken = loginRequest.getHeaders().get("Authorization").get(0);
//        String refreshToken = loginRequest.getHeaders().get("Refresh-token").get(0);
//
//        String url = "http://localhost:" + port + "/test";
//
//        //act
//
//        ResponseEntity<UUID> actual = restTemplate.
//                postForEntity(url, null, UUID.class);
//
//
//
//
//    }



    private void memberSave(String identity, String password){
        MemberSignUpRequestDto signUpRequestDto = new MemberSignUpRequestDto(identity,password);
        memberService.save(signUpRequestDto);
    }

}