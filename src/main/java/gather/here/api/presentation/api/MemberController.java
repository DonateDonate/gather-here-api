package gather.here.api.presentation.api;

import gather.here.api.application.dto.request.ModifyNicknameRequestDto;
import gather.here.api.application.dto.request.ModifyPasswordRequestDto;
import gather.here.api.application.dto.response.GetMemberResponseDto;
import gather.here.api.application.dto.response.UpdateImageResponseDto;
import gather.here.api.application.service.MemberService;
import gather.here.api.domain.security.CustomPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RestController
@Tag(name = "Member API", description = "Member API Docs")
public class MemberController {
    private final MemberService memberService;


    @GetMapping("/members")
    @Operation(summary = "Member 정보조회", description = "Member 정보조회")
    public ResponseEntity<GetMemberResponseDto> signUp(
            Authentication authentication
){
        CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();
        GetMemberResponseDto member = memberService.getMember(principal.getMemberSeq());
        return new ResponseEntity<>(member,HttpStatus.OK);
    }

    @PatchMapping("/members/nickname")
    @Operation(summary = "Member 닉네임 수정", description = "Member 닉네임 수정")
    public ResponseEntity<Object> modifyNickname(
            Authentication authentication,
            @Valid @RequestBody ModifyNicknameRequestDto request
            ){
        CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();
        memberService.modifyNickname(request, principal.getMemberSeq());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/members/password")
    @Operation(summary = "Member 비밀번호 수정", description = "Member 비밀번호 수정")
    public ResponseEntity<Object> modifyPassword(
            Authentication authentication,
            @Valid @RequestBody ModifyPasswordRequestDto request
    ){
        CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();
        memberService.modifyPassword(request, principal.getMemberSeq());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path = "/members/profile",consumes = "multipart/form-data")
    @Operation(summary = "Member profile image 등록", description = "Member profile image 등록 -> 기존에 등록한 이미지는 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = {@Content(schema = @Schema(implementation = UpdateImageResponseDto.class))})
    })
    public ResponseEntity<Object> saveImage(
            @RequestPart("imageFile") MultipartFile imageFile,
            Authentication authentication
    ){
        CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();
        UpdateImageResponseDto response = memberService.updateMemberImage(imageFile, principal.getMemberSeq());
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
