package gather.here.api.domain.repositories;

import gather.here.api.domain.entities.AppInfo;

public interface AppInfoRepository {
     AppInfo findByLatestAppInfo();
     AppInfo save(AppInfo appInfo);
}
