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

    public static LocationShareMessage from(LocationShareEvent locationShareEvent){
        List<MemberLocationRes> memberLocationResList  = new ArrayList<>();
        for(LocationShareEvent.MemberLocation memberLocation : locationShareEvent.getMemberLocations()){
            memberLocationResList.add(new MemberLocationRes(
                    memberLocation.getMemberSeq(),
                    memberLocation.getNickname(),
                    memberLocation.getImageUrl(),
                    memberLocation.getPresentLat(),memberLocation.getPresentLng()
                    ,memberLocation.getDestinationDistance(),
                    memberLocation.getIsOpen()
                    )
            );
        }
        return new LocationShareMessage(locationShareEvent.getRoomSeq(), memberLocationResList);
    }

    public static LocationShareMessage from(List<LocationShareEvent.MemberLocation> memberLocations, Long roomSeq){
        List<MemberLocationRes> memberLocationResList  = new ArrayList<>();
        for(LocationShareEvent.MemberLocation memberLocation : memberLocations){
            memberLocationResList.add(new MemberLocationRes(
                    memberLocation.getMemberSeq(),
                    memberLocation.getNickname(),
                    memberLocation.getImageUrl(),
                    memberLocation.getPresentLat(),memberLocation.getPresentLat(),memberLocation.getDestinationDistance(),
                    memberLocation.getIsOpen()
                    )
            );
        }
        return new LocationShareMessage(roomSeq, memberLocationResList);
    }
    private LocationShareMessage(Long roomSeq, List<MemberLocationRes> memberLocationResList) {
        this.roomSeq = roomSeq;
        this.memberLocationResList = memberLocationResList;
    }

    @Getter
     public static class MemberLocationRes{
        private Long memberSeq;
        private String nickname;
        private String imageUrl;
        private Double presentLat;
        private Double presentLng;
        private Double destinationDistance;
        private Boolean isOpen;

        public MemberLocationRes(Long memberSeq, String nickname, String imageUrl, Double presentLat, Double presentLng, Double destinationDistance,Boolean isOpen) {
            this.memberSeq = memberSeq;
            this.nickname = nickname;
            this.imageUrl = imageUrl;
            this.presentLat = presentLat;
            this.presentLng = presentLng;
            this.destinationDistance = destinationDistance;
            this.isOpen = isOpen;
        }
     }

}
