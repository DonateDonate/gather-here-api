package gather.here.api.domain.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SecurityMemberInfo {
    private String identity;
    private long memberSeq;
    private String password;
}
