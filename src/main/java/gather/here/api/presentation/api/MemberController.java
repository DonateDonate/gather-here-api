package gather.here.api.presentation.api;

import gather.here.api.application.dto.request.MemberSignUpRequestDto;
import gather.here.api.application.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/members")
    public ResponseEntity<Object> signUp(MemberSignUpRequestDto request){
        memberService.save(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
