package gather.here.api.domain.service;


import gather.here.api.application.dto.response.GetAppInfoResponseDto;
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

    public GetAppInfoResponseDto findLatestAppInfo(){
        AppInfo appInfo = appInfoRepository.findByLatestAppInfo();
        return new GetAppInfoResponseDto(appInfo.getVersion());
    }
}
