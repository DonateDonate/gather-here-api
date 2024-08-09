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
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

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
        MemberSignUpRequestDto requestDto = new MemberSignUpRequestDto("01012345674","12341234");
        String url = "http://localhost:" + port + "/members";

        //act
        ResponseEntity<Object> actual = restTemplate.postForEntity(url,requestDto, Object.class);

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
        ResponseEntity<Object> actual = restTemplate.postForEntity(url,signInRequestDto, Object.class);

        //assert
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getHeaders().get("Authorization").get(0)).isNotNull();
        assertThat(actual.getHeaders().get("Refresh-token").get(0)).isNotNull();
    }

    @Test
    @DisplayName("sut는 access-token으로 인증을 받는다")
    public void accessTokenAuthenticationTest(){

        //arrange
        String identity = "01012345670";
        String password = "12341234";
        memberSave(identity, password);

        MemberSignInRequestDto signInRequestDto = new MemberSignInRequestDto(identity,password);

        String loginUrl = "http://localhost:" + port + "/login";
        ResponseEntity<Object> loginRequest = restTemplate.postForEntity(loginUrl,signInRequestDto, Object.class);
        String accessToken = loginRequest.getHeaders().get("Authorization").get(0);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",accessToken);

        ResponseEntity<Object> actual = testGetMappingRequest(headers);

        //assertions
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("sut는 refresh token이 존재하면 새로운 access token을 발급하고 인증에 성공한다")
    public void reIssueTest(){
        //arrange
        String identity = "01012345679";
        String password = "12341234";
        memberSave(identity, password);

        MemberSignInRequestDto signInRequestDto = new MemberSignInRequestDto(identity,password);

        String loginUrl = "http://localhost:" + port + "/login";
        ResponseEntity<Object> loginRequest = restTemplate.postForEntity(loginUrl,signInRequestDto, Object.class);
        String accessToken = loginRequest.getHeaders().get("Authorization").get(0);
        String refreshToken = loginRequest.getHeaders().get("Refresh-token").get(0);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",accessToken);
        headers.set("Refresh-token",refreshToken);

        ResponseEntity<Object> refreshRequest = testGetMappingRequest(headers);

        String newAccessToken = refreshRequest.getHeaders().get("Authorization").get(0);
        HttpHeaders newHeaders = new HttpHeaders();
        newHeaders.set("Authorization","Bearer "+newAccessToken);

        //act
        ResponseEntity<Object> actual = testGetMappingRequest(newHeaders);

        //assertions
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    private ResponseEntity<Object> testGetMappingRequest(HttpHeaders headers) {
        HttpEntity<Object> request = new HttpEntity<>(null, headers);
        String url = "http://localhost:" + port + "/test/empty/request";

        ResponseEntity<Object> refreshRequest = restTemplate.exchange(
                url,
                HttpMethod.GET,request,
                Object.class);
        return refreshRequest;
    }


    private void memberSave(String identity, String password){
        MemberSignUpRequestDto signUpRequestDto = new MemberSignUpRequestDto(identity,password);
        memberService.save(signUpRequestDto);
    }

}