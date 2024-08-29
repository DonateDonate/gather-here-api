package gather.here.api.domain.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RedisHash(value = "locationShareEvent", timeToLive = 86400L)
@NoArgsConstructor
public class LocationShareEvent {

    @Id
    private Long roomSeq;
    private List<MemberLocation> memberLocations = new ArrayList<>();
    private Score score;
    private List<Long> destinationMemberList = new ArrayList<>();

    public LocationShareEvent create(Long roomSeq, Long memberSeq, String sessionId, String nickname, String imageUrl, Float presentLat, Float presentLng, Float destinationDistance){
        MemberLocation memberLocation = new MemberLocation(memberSeq,sessionId, nickname, imageUrl,presentLat,presentLng,destinationDistance);
        this.memberLocations.add(memberLocation);
        return new LocationShareEvent(roomSeq, memberLocations);
    }

    public List<String> getSessionIdList() {
        List<MemberLocation> memberLocations = this.getMemberLocations();
        return memberLocations.stream()
                .map(MemberLocation::getSessionId)
                .collect(Collectors.toList());
    }
    public List<Long> getScoreList(){
        return Arrays.asList(
                this.score.goldMemberSeq,
                this.score.getSilverMemberSeq(),
                this.score.getBronzeMemberSeq()
        );
    }

    public void addMemberLocations(Long memberSeq, String sessionId, String nickname, String imageUrl, Float presentLat, Float presentLng, Float destinationDistance){
        MemberLocation memberLocation = new MemberLocation(memberSeq, sessionId, nickname, imageUrl, presentLat, presentLng, destinationDistance);
        this.memberLocations.add(memberLocation);
    }

    public void removeMemberLocation(Long memberSeq){
        List<MemberLocation> removeMemberLocations = this.getMemberLocations().stream().filter(
                memberLocation -> !memberLocation.getMemberSeq().equals(memberSeq)
        ).collect(Collectors.toList());

        this.memberLocations = removeMemberLocations;
    }

    public void addDestinationMemberList(Long memberSeq){
        this.destinationMemberList.add(memberSeq);
    }

    public void removeDestinationMemberList(Long memberSeq){
        this.destinationMemberList.remove(memberSeq);
    }

    public void setGoldMemberSeq(Long memberSeq){
        Score score = new Score();
        score.setGoldMemberSeq(memberSeq);
        this.score =score;
    }

    public void setSilverMemberSeq(Long memberSeq){
        this.score.setSilverMemberSeq(memberSeq);
    }
    public void setBronzeMemberSeq(Long memberSeq){
        this.score.setBronzeMemberSeq(memberSeq);
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

        private MemberLocation(Long memberSeq, String sessionId,String nickname, String imageUrl, Float presentLat, Float presentLng, Float destinationDistance) {
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

       private void setGoldMemberSeq(Long goldMemberSeq) {
           this.goldMemberSeq = goldMemberSeq;
       }

       private void setSilverMemberSeq(Long silverMemberSeq) {
           this.silverMemberSeq = silverMemberSeq;
       }

       private void setBronzeMemberSeq(Long bronzeMemberSeq) {
           this.bronzeMemberSeq = bronzeMemberSeq;
       }
   }
}

