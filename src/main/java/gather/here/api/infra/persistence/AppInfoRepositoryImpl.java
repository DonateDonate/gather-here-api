package gather.here.api.infra.persistence;

import gather.here.api.domain.entities.AppInfo;
import gather.here.api.domain.repositories.AppInfoRepository;
import gather.here.api.infra.persistence.jpa.AppInfoJpaRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AppInfoRepositoryImpl implements AppInfoRepository {

    private final AppInfoJpaRepository appInfoJpaRepository;
    @Override
    public AppInfo findByLatestAppInfo() {
        return appInfoJpaRepository.findTop1ByOrderBySeqDesc();
    }

    @Override
    public AppInfo save(AppInfo appInfo) {
        return appInfoJpaRepository.save(appInfo);
    }
}
