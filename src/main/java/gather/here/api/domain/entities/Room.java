package gather.here.api.domain.entities;

import gather.here.api.domain.entities.base.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Room extends BaseTime {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Comment("member 식별값")
    private Long seq;

    @Comment("목적지 위도")
    private Float destinationLat;

    @Comment("목적지 경도")
    private Float destinationLng;

    @Comment("상태 진행중 : 1 종료 : 9")
    private int status;

    @Comment("만나는 날짜")
    private LocalDateTime encounterDate;

    @Comment("초대 코드")
    private String shareCode;

    @Comment("금메달 맴버")
    private Long goldMemberSeq;

    @Comment("은메달 맴버")
    private Long silverMemberSeq;

    @Comment("동메달 맴버")
    private Long bronzeMemberSeq;

    @OneToMany(mappedBy = "room")
    private List<Member> memberList = new ArrayList<>();
}
