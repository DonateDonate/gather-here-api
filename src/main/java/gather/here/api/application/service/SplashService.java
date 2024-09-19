package gather.here.api.application.service;

import gather.here.api.application.service.dto.response.GetSplashResponseDto;
import gather.here.api.domain.entities.AppInfo;
import gather.here.api.domain.service.AppInfoService;
import gather.here.api.domain.service.MemberService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SplashService {
    private final AppInfoService appInfoService;
    private final MemberService memberService;

    public GetSplashResponseDto getAppInfo(Long memberSeq) {

        AppInfo appInfo = appInfoService.findLatestAppInfo();
        boolean isJoinRoom = memberService.isJoinRoom(memberSeq);
        int status = 0;

        if (isJoinRoom) {
            status = 1;
        }
        return new GetSplashResponseDto(appInfo.getVersion(), status);
    }
}
