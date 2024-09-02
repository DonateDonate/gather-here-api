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
        Float presentLat = 34F;
        Float presentLng = 32F;
        Float destinationDistance = 200F;

        //act
        LocationShareEvent locationShareEvent = sut.create(roomSeq, memberSeq, sessionId, nickName, imageUrl, presentLat, presentLng, destinationDistance);

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
        Float presentLat = 34F;
        Float presentLng = 32F;
        Float destinationDistance = 200F;

        LocationShareEvent locationShareEvent = sut.create(roomSeq, memberSeq, sessionId, nickName, imageUrl, presentLat, presentLng, destinationDistance);

        //act
        Long addMemberSeq = 2L;
        String addSessionId = String.valueOf(UUID.randomUUID());
        String addNickName = "spring";
        String addImageUrl = "https://test/"+UUID.randomUUID();
        Float addPresentLat = 34F;
        Float addPresentLng = 32F;
        Float addDestinationDistance = 200F;

        locationShareEvent.addMemberLocations(
                addMemberSeq,
                addSessionId,
                addNickName,
                addImageUrl,
                addPresentLat,
                addPresentLng,
                addDestinationDistance
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
        Float presentLat = 34F;
        Float presentLng = 32F;
        Float destinationDistance = 200F;

        LocationShareEvent locationShareEvent = sut.create(roomSeq, memberSeq, sessionId, nickName, imageUrl, presentLat, presentLng, destinationDistance);

        Long addMemberSeq = 2L;
        String addSessionId = String.valueOf(UUID.randomUUID());
        String addNickName = "spring";
        String addImageUrl = "https://test/"+UUID.randomUUID();
        Float addPresentLat = 34F;
        Float addPresentLng = 32F;
        Float addDestinationDistance = 200F;

        locationShareEvent.addMemberLocations(
                addMemberSeq,
                addSessionId,
                addNickName,
                addImageUrl,
                addPresentLat,
                addPresentLng,
                addDestinationDistance
        );

        //act
        locationShareEvent.removeMemberLocation(addMemberSeq);

        //assert
        Assertions.assertThat(locationShareEvent.getMemberLocations().size()).isEqualTo(1);
        Assertions.assertThat(locationShareEvent.getMemberLocations().get(0).getMemberSeq()).isEqualTo(memberSeq);
    }

    @DisplayName("sut는 성공적으로 destinationMemberList에 값을 추가한다")
    @Test
    public void successAddDestinationMemberList(){
        //arrange
        LocationShareEvent sut = new LocationShareEvent();

        Long roomSeq = 1L;
        Long memberSeq = 1L;
        String sessionId = String.valueOf(UUID.randomUUID());
        String nickName = "spring";
        String imageUrl = "https://test/"+UUID.randomUUID();
        Float presentLat = 34F;
        Float presentLng = 32F;
        Float destinationDistance = 200F;

        LocationShareEvent locationShareEvent = sut.create(roomSeq, memberSeq, sessionId, nickName, imageUrl, presentLat, presentLng, destinationDistance);

        //act
        locationShareEvent.addDestinationMemberList(memberSeq);

        //assert
        Assertions.assertThat(locationShareEvent.getDestinationMemberList().size()).isEqualTo(1);
        Assertions.assertThat(locationShareEvent.getDestinationMemberList().get(0)).isEqualTo(memberSeq);
    }

    @DisplayName("sut는 성공적으로 요청된 memberSeq 값을 destinationMemberList에서 삭제한다")
    @Test
    public void successRemoveDestinationMemberList(){
        //arrange
        LocationShareEvent sut = new LocationShareEvent();

        Long roomSeq = 1L;
        Long memberSeq = 1L;
        String sessionId = String.valueOf(UUID.randomUUID());
        String nickName = "spring";
        String imageUrl = "https://test/"+UUID.randomUUID();
        Float presentLat = 34F;
        Float presentLng = 32F;
        Float destinationDistance = 200F;

        LocationShareEvent locationShareEvent = sut.create(roomSeq, memberSeq, sessionId, nickName, imageUrl, presentLat, presentLng, destinationDistance);

        //act
        locationShareEvent.removeDestinationMemberList(memberSeq);

        //assert
        Assertions.assertThat(locationShareEvent.getDestinationMemberList().size()).isEqualTo(0);
    }

    @DisplayName("sut는 socre 객체에 memberSeq값이 정상적으로 들어간다")
    @Test
    public void setScoreTest(){
        //arrange
        LocationShareEvent sut = new LocationShareEvent();

        Long roomSeq = 1L;
        Long memberSeq = 1L;
        String sessionId = String.valueOf(UUID.randomUUID());
        String nickName = "spring";
        String imageUrl = "https://test/"+UUID.randomUUID();
        Float presentLat = 34F;
        Float presentLng = 32F;
        Float destinationDistance = 200F;

        Long silverMemberSeq = 2L;
        Long bronzeMemberSeq = 3L;

        LocationShareEvent locationShareEvent = sut.create(roomSeq, memberSeq, sessionId, nickName, imageUrl, presentLat, presentLng, destinationDistance);

        //act
        locationShareEvent.setGoldMemberSeq(memberSeq);
        locationShareEvent.setSilverMemberSeq(silverMemberSeq);
        locationShareEvent.setBronzeMemberSeq(bronzeMemberSeq);

        //assert
        Assertions.assertThat(locationShareEvent.getScore().getGoldMemberSeq()).isEqualTo(memberSeq);
        Assertions.assertThat(locationShareEvent.getScore().getSilverMemberSeq()).isEqualTo(silverMemberSeq);
        Assertions.assertThat(locationShareEvent.getScore().getBronzeMemberSeq()).isEqualTo(bronzeMemberSeq);
    }






}