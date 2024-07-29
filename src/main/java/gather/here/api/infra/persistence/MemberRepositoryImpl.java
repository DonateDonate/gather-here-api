package gather.here.api.infra.persistence;

import gather.here.api.domain.entities.Member;
import gather.here.api.domain.repositories.MemberRepository;
import gather.here.api.infra.persistence.jpa.MemberJpaRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberJpaRepository memberJpaRepository;

    @Override
    public void save(Member member) {
        memberJpaRepository.save(member);
    }
}
