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
        String imageUrl = "https://test/"+String.valueOf(UUID.randomUUID());
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
}