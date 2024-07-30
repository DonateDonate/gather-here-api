package gather.here.api.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;
    private Long memberSeq;
    private String refreshToken;

    private Token(Long memberSeq, String refreshToken) {
        this.memberSeq = memberSeq;
        this.refreshToken = refreshToken;
    }
    public static Token create(Long memberSeq, String refreshToken){
        return new Token(memberSeq,refreshToken);
    }

}
