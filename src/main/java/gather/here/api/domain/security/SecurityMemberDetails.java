package gather.here.api.domain.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class SecurityMemberDetails implements UserDetails {

    private final SecurityMemberInfo securityMemberInfo;

    public SecurityMemberDetails(SecurityMemberInfo securityMemberInfo) {
        this.securityMemberInfo = securityMemberInfo;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return securityMemberInfo.getPassword();
    }

    @Override
    public String getUsername() {
        return securityMemberInfo.getIdentity();
    }

    public Long getMemberSeq() {
        return securityMemberInfo.getMemberSeq();
    }

}
