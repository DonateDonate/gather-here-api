package gather.here.api.presentation.api;

import gather.here.api.application.dto.request.MemberSignInRequestDto;
import gather.here.api.application.dto.request.MemberSignUpRequestDto;
import gather.here.api.application.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AuthController {
    private final MemberService memberService;

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
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/test")
    public ResponseEntity<Object> test(
            Authentication authentication
    ){
        log.info("principal ={}", authentication.getPrincipal());
        //UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return new ResponseEntity<>(authentication.getPrincipal(),HttpStatus.OK);
    }
}
