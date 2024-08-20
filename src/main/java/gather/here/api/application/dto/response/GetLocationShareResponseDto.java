package gather.here.api.application.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class GetLocationShareResponseDto {
    private Long roomSeq;
    private List<MemberLocationRes> memberLocationResList;
    private ScoreRes score;


//    public static GetLocationShareResponseDto create(LocationShareEvent locationShareEvent){
//        Long roomSeq = locationShareEvent.getRoomSeq();
//
//
//
//
//    }

    public GetLocationShareResponseDto(Long roomSeq, List<MemberLocationRes> memberLocationResList, ScoreRes score) {
        this.roomSeq = roomSeq;
        this.memberLocationResList = memberLocationResList;
        this.score = score;
    }

    public static MemberLocationRes createMemberLocation(Long memberSeq, String username, String imageUrl, String presentLat, String presentLng, String destinationDistance){
        return new MemberLocationRes(memberSeq,username,imageUrl,presentLat,presentLng,destinationDistance);
    }
    public static ScoreRes createScoreRes(Long goldMemberSeq, Long silverMemberSeq, Long bronzeMemberSeq){
        return new ScoreRes(goldMemberSeq, silverMemberSeq, bronzeMemberSeq);
    }
}

class MemberLocationRes{
    private Long MemberSeq;
    private String username;
    private String imageUrl;
    private String presentLat;

    private String presentLng;
    private String destinationDistance;

    public MemberLocationRes(Long memberSeq, String username, String imageUrl, String presentLat, String presentLng, String destinationDistance) {
        MemberSeq = memberSeq;
        this.username = username;
        this.imageUrl = imageUrl;
        this.presentLat = presentLat;
        this.presentLng = presentLng;
        this.destinationDistance = destinationDistance;
    }
}

class ScoreRes{
    private Long goldMemberSeq;
    private Long silverMemberSeq;
    private Long bronzeMemberSeq;

    public ScoreRes(Long goldMemberSeq, Long silverMemberSeq, Long bronzeMemberSeq) {
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
