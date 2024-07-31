package gather.here.api.presentation.api;

import gather.here.api.application.dto.request.MemberSignInRequestDto;
import gather.here.api.application.dto.request.MemberSignUpRequestDto;
import gather.here.api.application.dto.response.GetTokenResponseDto;
import gather.here.api.application.service.AuthService;
import gather.here.api.application.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MemberController {
    private final MemberService memberService;
    private final AuthService authService;

    @PostMapping("/members")
    public ResponseEntity<Object> signUp(
             @RequestBody MemberSignUpRequestDto request
){
        memberService.save(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> signIn(
            @RequestBody MemberSignInRequestDto memberSignInRequestDto
            ){
        GetTokenResponseDto token = authService.getToken(memberSignInRequestDto);
        return new ResponseEntity<>(token,HttpStatus.OK);
    }

    @GetMapping("/test")
    public ResponseEntity<Object> test(
    ){
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
