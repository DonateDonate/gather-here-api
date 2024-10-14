package gather.here.api.domain.entities;

import gather.here.api.global.exception.MemberException;
import gather.here.api.global.exception.ResponseStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.http.HttpStatus;

import static gather.here.api.global.util.NicknameMachine.getRandomNickname;

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
    private String identity;

    @Comment("비밀번호")
    private String password;

    @Comment("닉네임")
    private String nickname;

    @Comment("이미지 key - uuid")
    private String imageKey;

    @Comment("푸쉬알림 토큰")
    private String pushToken;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_seq", nullable = true)
    private Room room;

    @Comment("활성화 유무")
    private boolean isActive;

    private Member(String identity, String password, String nickname) {
        this.identity = identity;
        this.password = password;
        this.nickname =nickname;
        this.isActive = true;
    }

    public static Member create(String id, String password) {
        return new Member(id, password, getRandomNickname());
    }

    public static void assertPassword(String password) {
        if(password.length()<4 || password.length() >8){
            throw new MemberException(ResponseStatus.INCORRECT_PASSWORD, HttpStatus.BAD_REQUEST);
        }
    }

    public static void assertMemberIdentity(String identity) {
        if(identity.length() != 11){
            throw new MemberException(ResponseStatus.INCORRECT_IDENTITY, HttpStatus.BAD_REQUEST);
        }
    }

    public void joinRoom(Room room) {
        this.room = room;
    }

    public void exitRoom(){
        this.room = null;
    }

    public void setNickname(String newNickname){
        if(StringUtils.isEmpty(newNickname) || newNickname.length()>8){
            throw new MemberException(ResponseStatus.UNCORRECTED_MEMBER_NICKNAME, HttpStatus.BAD_REQUEST);
        }
        this.nickname = newNickname;
    }

    public void modifyPassword(String password,String encodedPassword){
            if (password.length() < 4 || password.length() > 8) {
                throw new MemberException(ResponseStatus.INCORRECT_PASSWORD, HttpStatus.BAD_REQUEST);
            }

            this.password = encodedPassword;
    }

    public void setImageKey(String imageKey){
        if(StringUtils.isEmpty(imageKey)){
            throw new MemberException(ResponseStatus.INCORRECT_IMAGE_KEY, HttpStatus.BAD_REQUEST);
        }
        this.imageKey = imageKey;
    }
    public void cancelAccount(){
        this.isActive  = false;
    }

}
