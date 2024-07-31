package gather.here.api.domain.repositories;

import gather.here.api.domain.entities.Member;

import java.util.Optional;

public interface MemberRepository {
     Member save(Member member);
     Optional<Member> findByIdentity(String id);
}
