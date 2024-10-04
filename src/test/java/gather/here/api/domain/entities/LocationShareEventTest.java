package gather.here.api.domain.entities;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class LocationShareEventTest {

    @DisplayName("sut는 성공적으로 locationShareEvent 인스턴스를 생성한다")
    @Test
    public void successCreateLocationShareEventTest(){
        //arrange
        LocationShareEvent sut = new LocationShareEvent();
        Long roomSeq = 1L;
        Long memberSeq = 1L;
        String sessionId = String.valueOf(UUID.randomUUID());
        String nickName = "spring";
        String imageUrl = "https://test/"+UUID.randomUUID();
        Double presentLat = 34.0;
        Double presentLng = 32.0;
        Double destinationDistance = 200.0;

        //act
        LocationShareEvent locationShareEvent = sut.create(roomSeq, memberSeq, sessionId, nickName, imageUrl, presentLat, presentLng, destinationDistance,null);

        //assert
        Assertions.assertThat(locationShareEvent.getRoomSeq()).isEqualTo(roomSeq);
        Assertions.assertThat(locationShareEvent.getMemberLocations().get(0).getMemberSeq()).isEqualTo(memberSeq);
        Assertions.assertThat(locationShareEvent.getMemberLocations().get(0).getSessionId()).isEqualTo(sessionId);
        Assertions.assertThat(locationShareEvent.getMemberLocations().get(0).getNickname()).isEqualTo(nickName);
        Assertions.assertThat(locationShareEvent.getMemberLocations().get(0).getImageUrl()).isEqualTo(imageUrl);
        Assertions.assertThat(locationShareEvent.getMemberLocations().get(0).getPresentLat()).isEqualTo(presentLat);
        Assertions.assertThat(locationShareEvent.getMemberLocations().get(0).getPresentLng()).isEqualTo(presentLng);
        Assertions.assertThat(locationShareEvent.getMemberLocations().get(0).getDestinationDistance()).isEqualTo(destinationDistance);
    }

    @DisplayName("sut는 성공적으로 memberLocation을 추가한다")
    @Test
    public void successAddMemberLocationTest(){
        //arrange
        LocationShareEvent sut = new LocationShareEvent();

        Long roomSeq = 1L;
        Long memberSeq = 1L;
        String sessionId = String.valueOf(UUID.randomUUID());
        String nickName = "spring";
        String imageUrl = "https://test/"+UUID.randomUUID();
        Double presentLat = 34.6;
        Double presentLng = 32.2;
        Double destinationDistance = 200.2;

        LocationShareEvent locationShareEvent = sut.create(roomSeq, memberSeq, sessionId, nickName, imageUrl, presentLat, presentLng, destinationDistance,null);

        //act
        Long addMemberSeq = 2L;
        String addSessionId = String.valueOf(UUID.randomUUID());
        String addNickName = "spring";
        String addImageUrl = "https://test/"+UUID.randomUUID();
        Double addPresentLat = 34.2;
        Double addPresentLng = 32.4;
        Double addDestinationDistance = 200.2;

        locationShareEvent.addMemberLocations(
                addMemberSeq,
                addSessionId,
                addNickName,
                addImageUrl,
                addPresentLat,
                addPresentLng,
                addDestinationDistance,
                null
        );
        //assert
        Assertions.assertThat(locationShareEvent.getMemberLocations().size()).isEqualTo(2);
        Assertions.assertThat(locationShareEvent.getMemberLocations().get(0).getMemberSeq()).isEqualTo(memberSeq);
        Assertions.assertThat(locationShareEvent.getMemberLocations().get(1).getMemberSeq()).isEqualTo(addMemberSeq);
    }

    @DisplayName("sut는 성공적으로 요청된 memberSeq 값을 memberLocationList에서 삭제한다")
    @Test
    public void successRemoveMemberLocationTest(){

        //arrange
        LocationShareEvent sut = new LocationShareEvent();

        Long roomSeq = 1L;
        Long memberSeq = 1L;
        String sessionId = String.valueOf(UUID.randomUUID());
        String nickName = "spring";
        String imageUrl = "https://test/"+UUID.randomUUID();
        Double presentLat = 34.1;
        Double presentLng = 32.1;
        Double destinationDistance = 200.2;

        LocationShareEvent locationShareEvent = sut.create(roomSeq, memberSeq, sessionId, nickName, imageUrl, presentLat, presentLng, destinationDistance,null);

        Long addMemberSeq = 2L;
        String addSessionId = String.valueOf(UUID.randomUUID());
        String addNickName = "spring";
        String addImageUrl = "https://test/"+UUID.randomUUID();
        Double addPresentLat = 34.2;
        Double addPresentLng = 32.3;
        Double addDestinationDistance = 200.5;

        locationShareEvent.addMemberLocations(
                addMemberSeq,
                addSessionId,
                addNickName,
                addImageUrl,
                addPresentLat,
                addPresentLng,
                addDestinationDistance,
                null
        );

        //act
        locationShareEvent.removeMemberLocation(addMemberSeq);

        //assert
        Assertions.assertThat(locationShareEvent.getMemberLocations().size()).isEqualTo(1);
        Assertions.assertThat(locationShareEvent.getMemberLocations().get(0).getMemberSeq()).isEqualTo(memberSeq);
    }
}