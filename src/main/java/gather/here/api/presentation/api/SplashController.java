package gather.here.api.presentation.api;

import gather.here.api.application.service.SplashService;
import gather.here.api.application.service.dto.response.GetSplashResponseDto;
import gather.here.api.domain.security.CustomPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@Tag(name = "AppInfo API", description = "Splash API Docs")
public class SplashController {
    private final SplashService splashService;

    @GetMapping("/appInfos")
    @Operation(summary = "app 정보 가져오기", description = "app 정보 가져오기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = {@Content(schema = @Schema(implementation = GetSplashResponseDto.class))})
    })
    public ResponseEntity<Object> find(Authentication authentication) {

        CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();
        GetSplashResponseDto response = splashService.getAppInfo(principal.getMemberSeq());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
