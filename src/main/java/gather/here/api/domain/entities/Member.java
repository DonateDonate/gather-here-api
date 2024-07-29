package gather.here.api.domain.entities;

import gather.here.api.domain.entities.base.BaseTime;
import gather.here.api.infra.exception.MemberException;
import gather.here.api.infra.exception.ResponseStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.http.HttpStatus;

@Getter
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
@Entity
public class Member extends BaseTime {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "member_seq")
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

    @Comment("활성화 유무")
    private boolean isActive;

    private Member(String id, String password) {
        this.id = id;
        this.password = password;
        this.isActive = true;
    }
    //유저를 생성한다
    public static Member create(String id, String password,String encodedPassword){
        if(password.length()<4 || password.length() >8){
            throw new MemberException(ResponseStatus.INVALID_INPUT, HttpStatus.BAD_REQUEST);
        }

        if(id.length() != 11){
            throw new MemberException(ResponseStatus.INVALID_INPUT, HttpStatus.BAD_REQUEST);
        }


        return new Member(id,encodedPassword);
    }

}
