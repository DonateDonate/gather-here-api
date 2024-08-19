package gather.here.api.domain.security;

import lombok.Getter;

@Getter
public class CustomPrincipal {
    private String identity;
    private Long memberSeq;

    public CustomPrincipal(String identity, Long memberSeq) {
        this.identity = identity;
        this.memberSeq = memberSeq;
    }
}
