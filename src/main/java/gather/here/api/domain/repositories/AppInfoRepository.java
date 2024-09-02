package gather.here.api.domain.repositories;

import gather.here.api.domain.entities.AppInfo;

public interface AppInfoRepository {
     AppInfo getByLatestAppInfo();
     AppInfo save(AppInfo appInfo);
}
