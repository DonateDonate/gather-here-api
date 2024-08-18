package gather.here.api.presentation.api;

import gather.here.api.application.dto.response.GetAppInfoResponseDto;
import gather.here.api.application.service.AppInfoService;
import gather.here.api.domain.entities.AppInfo;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@Tag(name = "AppInfo API", description = "AppInfo API Docs")
public class AppInfoController {
    private final AppInfoService appInfoService;


    @GetMapping("/appInfos")
    @Operation(summary = "app 정보 가져오기", description = "app 정보 가져오기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = {@Content(schema = @Schema(implementation = GetAppInfoResponseDto.class))})
    })
    public ResponseEntity<Object> find(
){
        appInfoService.saveAppInfo(AppInfo.create("1.1.0"));
        GetAppInfoResponseDto latestAppInfo = appInfoService.findLatestAppInfo();
        return new ResponseEntity<>(latestAppInfo,HttpStatus.OK);
    }

}
