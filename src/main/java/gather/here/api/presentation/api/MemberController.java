package gather.here.api.presentation.api;

import gather.here.api.application.dto.request.ModifyNicknameRequestDto;
import gather.here.api.application.dto.request.ModifyPasswordRequestDto;
import gather.here.api.application.dto.response.GetMemberResponseDto;
import gather.here.api.application.dto.response.UpdateImageResponseDto;
import gather.here.api.application.service.MemberService;
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
        GetMemberResponseDto member = memberService.getMember(String.valueOf(authentication.getPrincipal()));
        return new ResponseEntity<>(member,HttpStatus.OK);
    }

    @PatchMapping("/members/nickname")
    @Operation(summary = "Member 닉네임 수정", description = "Member 닉네임 수정")
    public ResponseEntity<Object> modifyNickname(
            Authentication authentication,
            @Valid @RequestBody ModifyNicknameRequestDto request
            ){

        memberService.modifyNickname(request, String.valueOf(authentication.getPrincipal()));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/members/password")
    @Operation(summary = "Member 비밀번호 수정", description = "Member 비밀번호 수정")
    public ResponseEntity<Object> modifyPassword(
            Authentication authentication,
            @Valid @RequestBody ModifyPasswordRequestDto request
    ){

        memberService.modifyPassword(request, String.valueOf(authentication.getPrincipal()));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path = "/members/profile",consumes = "multipart/form-data")
    @Operation(summary = "Member profile image 등록", description = "Member profile image 등록 -> 기존에 등록한 이미지는 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = {@Content(schema = @Schema(implementation = UpdateImageResponseDto.class))})
    })
    public ResponseEntity<Object> saveImage(
            @RequestPart MultipartFile imageFile,
            Authentication authentication
    ){
        UpdateImageResponseDto response = memberService.updateMemberImage(imageFile, String.valueOf(authentication.getPrincipal()));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

}
