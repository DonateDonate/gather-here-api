package gather.here.api.infra.persistence.jpa;

import gather.here.api.domain.entities.AppInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppInfoJpaRepository extends JpaRepository<AppInfo,Long> {
    AppInfo findTop1ByOrderBySeqDesc();
}
