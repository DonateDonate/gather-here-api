package gather.here.api.application.dto.response;

import lombok.Getter;

@Getter
public class GetLocationShareResponseDto {

    private MemberLocationRes memberLocation;
    private ScoreRes score;

    public GetLocationShareResponseDto(Long memberSeq, String username, String imageUrl, String presentLat, String presentLng, String destinationDistance) {
        this.memberLocation = createMemberLocation(memberSeq,username,imageUrl,presentLat,presentLng,destinationDistance);
        this.score = new ScoreRes();
    }

    public static MemberLocationRes createMemberLocation(Long memberSeq, String username, String imageUrl, String presentLat, String presentLng, String destinationDistance){
        return new MemberLocationRes(memberSeq,username,imageUrl,presentLat,presentLng,destinationDistance);
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
