package gather.here.api.infra.persistence.redis;

import gather.here.api.domain.entities.LocationShareEvent;
import org.springframework.data.repository.CrudRepository;

public interface LocationShareEventRedisRepository extends CrudRepository<LocationShareEvent,Long> {
}
