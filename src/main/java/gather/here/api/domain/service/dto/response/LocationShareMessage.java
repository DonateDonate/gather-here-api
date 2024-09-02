package gather.here.api.domain.service.dto.response;

import gather.here.api.domain.entities.LocationShareEvent;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

//GetLocationShareResponseDto
@Getter
public class LocationShareMessage {
    private Long roomSeq;
    private List<MemberLocationRes> memberLocationResList;
    private ScoreRes scoreRes;

    public static LocationShareMessage from(LocationShareEvent locationShareEvent){
        List<MemberLocationRes> memberLocationResList  = new ArrayList<>();
        for(LocationShareEvent.MemberLocation memberLocation : locationShareEvent.getMemberLocations()){
            memberLocationResList.add(new MemberLocationRes(
                    memberLocation.getMemberSeq(),
                    memberLocation.getNickname(),
                    memberLocation.getImageUrl(),
                    memberLocation.getPresentLat(),memberLocation.getPresentLat(),memberLocation.getDestinationDistance())
            );
        }
        if(locationShareEvent.getScore() != null){
            LocationShareEvent.Score score = locationShareEvent.getScore();
            ScoreRes scoreRes = new ScoreRes(score.getGoldMemberSeq(), score.getSilverMemberSeq(), score.getBronzeMemberSeq());

            return new LocationShareMessage(locationShareEvent.getRoomSeq(), memberLocationResList,scoreRes);
        }
        return new LocationShareMessage(locationShareEvent.getRoomSeq(), memberLocationResList);
    }

    public static LocationShareMessage from(List<LocationShareEvent.MemberLocation> memberLocations, LocationShareEvent.Score score, Long roomSeq){
        List<MemberLocationRes> memberLocationResList  = new ArrayList<>();
        for(LocationShareEvent.MemberLocation memberLocation : memberLocations){
            memberLocationResList.add(new MemberLocationRes(
                    memberLocation.getMemberSeq(),
                    memberLocation.getNickname(),
                    memberLocation.getImageUrl(),
                    memberLocation.getPresentLat(),memberLocation.getPresentLat(),memberLocation.getDestinationDistance())
            );
        }

        ScoreRes scoreRes = new ScoreRes(score.getGoldMemberSeq(), score.getSilverMemberSeq(), score.getBronzeMemberSeq());

        return new LocationShareMessage(roomSeq, memberLocationResList,scoreRes);
    }


    private LocationShareMessage(Long roomSeq, List<MemberLocationRes> memberLocationResList, ScoreRes scoreRes) {
        this.roomSeq = roomSeq;
        this.memberLocationResList = memberLocationResList;
        this.scoreRes = scoreRes;
    }

    private LocationShareMessage(Long roomSeq, List<MemberLocationRes> memberLocationResList) {
        this.roomSeq = roomSeq;
        this.memberLocationResList = memberLocationResList;
        this.scoreRes = new ScoreRes();
    }

    @Getter
     public static class MemberLocationRes{
        private Long memberSeq;
        private String nickname;
        private String imageUrl;
        private Float presentLat;
        private Float presentLng;
        private Float destinationDistance;

        public MemberLocationRes(Long memberSeq, String nickname, String imageUrl, Float presentLat, Float presentLng, Float destinationDistance) {
            this.memberSeq = memberSeq;
            this.nickname = nickname;
            this.imageUrl = imageUrl;
            this.presentLat = presentLat;
            this.presentLng = presentLng;
            this.destinationDistance = destinationDistance;
        }
     }

     @Getter
    public static class ScoreRes {
        private Long goldMemberSeq;
        private Long silverMemberSeq;
        private Long bronzeMemberSeq;


        public ScoreRes(Long goldMemberSeq, Long silverMemberSeq, Long bronzeMemberSeq) {
            this.goldMemberSeq = goldMemberSeq;
            this.silverMemberSeq = silverMemberSeq;
            this.bronzeMemberSeq = bronzeMemberSeq;
        }

        public ScoreRes() {
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
