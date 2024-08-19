package gather.here.api.domain.security;

import gather.here.api.domain.entities.Member;
import gather.here.api.domain.repositories.MemberRepository;
import gather.here.api.global.exception.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Member member = memberRepository.findByIdentity(username)
                .orElseThrow(() -> new UsernameNotFoundException(ResponseStatus.NOT_FOUND.getMessage()));

        return User.builder()
                .username(member.getIdentity())
                .password(member.getPassword())
                .build();
    }
}
