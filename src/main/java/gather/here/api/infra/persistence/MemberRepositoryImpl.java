package gather.here.api.infra.persistence;

import gather.here.api.domain.entities.Member;
import gather.here.api.domain.repositories.MemberRepository;
import gather.here.api.global.exception.MemberException;
import gather.here.api.global.exception.ResponseStatus;
import gather.here.api.infra.persistence.jpa.MemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Optional;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Member save(Member member) {
        return memberJpaRepository.save(member);
    }

    @Override
    public Optional<Member> findByIdentityAndIsActiveTrue(String identity) {
       return Optional.ofNullable(memberJpaRepository.findByIdentityAndIsActiveTrue(identity));
    }

    @Override
    public Member getBySeq(Long memberSeq) {
        return memberJpaRepository.findById(memberSeq).orElseThrow(
                ()-> new MemberException(ResponseStatus.NOT_FOUND_MEMBER, HttpStatus.FORBIDDEN)
        );
    }
}
