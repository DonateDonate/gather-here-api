package gather.here.api.domain.entities;

import gather.here.api.domain.entities.base.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member extends BaseTime {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Comment("member 식별값")
    private Long seq;

    @Comment("아이디")
    private String id;

    @Comment("비밀번호")
    private String password;

    @Comment("닉네임")
    private String nickname;

    @Comment("이미지키 - uuid")
    private String imageKey;

    @Comment("푸쉬알림 토큰")
    private String pushToken;

    @Comment("socket session id")
    private String socketId;

    @Comment("인증 토큰")
    private String accessToken;

    @Comment("인증 refresh token")
    private String refreshToken;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_seq")
    private Room room;
}
