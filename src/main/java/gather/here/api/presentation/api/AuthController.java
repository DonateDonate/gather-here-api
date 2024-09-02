package gather.here.api.presentation.api;

import gather.here.api.application.dto.request.MemberSignInRequestDto;
import gather.here.api.application.dto.request.MemberSignUpRequestDto;
import gather.here.api.domain.service.MemberService;
import gather.here.api.domain.security.CustomPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@Tag(name = "Auth API", description = "Auth API Docs")
public class AuthController {
    private final MemberService memberService;


    @PostMapping("/members")
    @Operation(summary = "Member 회원가입", description = "Member 회원가입")
    public ResponseEntity<Object> signUp(
             @Valid @RequestBody MemberSignUpRequestDto request
){
        memberService.save(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/login")
    @Operation(summary = "Member 로그인", description = "Member 로그인")
    public ResponseEntity<Object> signIn(
            @Valid @RequestBody MemberSignInRequestDto memberSignInRequestDto
            ){
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/members")
    @Operation(summary = "Member 회원탈퇴", description = "Member 회원탈퇴")
    public ResponseEntity<Object> cancelAccount(
            Authentication authentication
    ){
        CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();
        memberService.cancelAccount(principal.getMemberSeq());

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
