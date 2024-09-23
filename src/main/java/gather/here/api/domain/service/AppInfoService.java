package gather.here.api.domain.service;


import gather.here.api.application.service.dto.response.GetSplashResponseDto;
import gather.here.api.domain.entities.AppInfo;
import gather.here.api.domain.repositories.AppInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class AppInfoService {
    private final AppInfoRepository appInfoRepository;

    @Transactional
    public void saveAppInfo(AppInfo appInfo){
        appInfoRepository.save(appInfo);
    }

    public GetSplashResponseDto getAppInfo(Long memberSeq) {
        AppInfo appInfo = appInfoRepository.getByLatestAppInfo();
        return new GetSplashResponseDto(appInfo.getVersion());
    }
}
