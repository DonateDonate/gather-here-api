package gather.here.api.domain.entities;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.ArrayList;
import java.util.List;

@Getter
@RedisHash(value = "locationShareEvent", timeToLive = 30)
public class LocationShareEvent {

    @Id
    private Long roomSeq;
    private List<MemberLocation> memberLocations;
    private Score score;

    public static LocationShareEvent create(Long roomSeq,Long memberSeq, String memberSessionId,String nickname, String imageUrl, Float presentLat, Float presentLng, Float destinationDistance){
        List<MemberLocation> memberLocations = new ArrayList<>();

        MemberLocation memberLocation = new MemberLocation(memberSeq,memberSessionId, nickname, imageUrl,presentLat,presentLng,destinationDistance);
        memberLocations.add(memberLocation);
        return new LocationShareEvent(roomSeq,memberLocations, new Score());
    }

    public LocationShareEvent(Long roomSeq, List<MemberLocation> memberLocations, Score score) {
        this.roomSeq = roomSeq;
        this.memberLocations = memberLocations;
        this.score = score;
    }
}

@Getter
class MemberLocation{
    private Long memberSeq;
    private String memberSessionId;
    private String nickname;
    private String imageUrl;
    private Float presentLat;
    private Float presentLng;
    private Float destinationDistance;

    public MemberLocation(Long memberSeq, String memberSessionId,String nickname, String imageUrl, Float presentLat, Float presentLng, Float destinationDistance) {
        this.memberSeq = memberSeq;
        this.memberSessionId = memberSessionId;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
        this.presentLat = presentLat;
        this.presentLng = presentLng;
        this.destinationDistance = destinationDistance;
    }
}

@Getter
class Score{
    private Long goldMemberSeq;
    private Long silverMemberSeq;
    private Long bronzeMemberSeq;

    public Score() {
    }
}
