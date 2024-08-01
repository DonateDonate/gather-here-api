package gather.here.api.presentation.api;

import gather.here.api.application.dto.request.MemberSignInRequestDto;
import gather.here.api.application.dto.request.MemberSignUpRequestDto;
import gather.here.api.application.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    ){
        System.out.println();
        System.out.println();
        System.out.println();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
