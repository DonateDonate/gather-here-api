package gather.here.api.domain.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RedisHash(value = "locationShareEvent", timeToLive = 86400L)
@NoArgsConstructor
public class LocationShareEvent {

    @Id
    private Long roomSeq;
    private List<MemberLocation> memberLocations;
    private Score score;

    public static LocationShareEvent create(Long roomSeq, Long memberSeq, String sessionId, String nickname, String imageUrl, Float presentLat, Float presentLng, Float destinationDistance){
        List<MemberLocation> memberLocations = new ArrayList<>();

        MemberLocation memberLocation = new MemberLocation(memberSeq,sessionId, nickname, imageUrl,presentLat,presentLng,destinationDistance);
        memberLocations.add(memberLocation);
        LocationShareEvent locationShareEvent = new LocationShareEvent(roomSeq, memberLocations);
        locationShareEvent.setScore(new Score());
        return locationShareEvent;
    }

    public List<String> getSessionIdList() {
        List<MemberLocation> memberLocations = this.getMemberLocations();
        return memberLocations.stream()
                .map(MemberLocation::getSessionId)
                .collect(Collectors.toList());
    }

    public void addMemberLocations(Long memberSeq, String sessionId,String nickname, String imageUrl, Float presentLat, Float presentLng, Float destinationDistance){
        MemberLocation memberLocation = new MemberLocation(memberSeq, sessionId, nickname, imageUrl, presentLat, presentLng, destinationDistance);
        this.memberLocations.add(memberLocation);
    }

    public void setScore(Score score){
        this.score = score;
    }

    private LocationShareEvent(Long roomSeq, List<MemberLocation> memberLocations, Score score) {
        this.roomSeq = roomSeq;
        this.memberLocations = memberLocations;
        this.score = score;
    }

    private LocationShareEvent(Long roomSeq, List<MemberLocation> memberLocations) {
        this.roomSeq = roomSeq;
        this.memberLocations = memberLocations;
    }
    @NoArgsConstructor
    @Getter
    public static class MemberLocation{
        private Long memberSeq;
        private String sessionId;
        private String nickname;
        private String imageUrl;
        private Float presentLat;
        private Float presentLng;
        private Float destinationDistance;

        public MemberLocation(Long memberSeq, String sessionId,String nickname, String imageUrl, Float presentLat, Float presentLng, Float destinationDistance) {
            this.memberSeq = memberSeq;
            this.sessionId = sessionId;
            this.nickname = nickname;
            this.imageUrl = imageUrl;
            this.presentLat = presentLat;
            this.presentLng = presentLng;
            this.destinationDistance = destinationDistance;
        }

    }


   @Getter
   @NoArgsConstructor
   public static class Score{
        private Long goldMemberSeq;
        private Long silverMemberSeq;
        private Long bronzeMemberSeq;

        public Score(Long goldMemberSeq, Long silverMemberSeq, Long bronzeMemberSeq) {
            this.goldMemberSeq = goldMemberSeq;
            this.silverMemberSeq = silverMemberSeq;
            this.bronzeMemberSeq = bronzeMemberSeq;
        }

       public void setGoldMemberSeq(Long goldMemberSeq) {
           this.goldMemberSeq = goldMemberSeq;
       }

       public void setSilverMemberSeq(Long silverMemberSeq) {
           this.silverMemberSeq = silverMemberSeq;
       }

       public void setBronzeMemberSeq(Long bronzeMemberSeq) {
           this.bronzeMemberSeq = bronzeMemberSeq;
       }
   }
}

