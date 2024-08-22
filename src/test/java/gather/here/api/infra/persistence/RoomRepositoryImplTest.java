package gather.here.api.infra.persistence;

import gather.here.api.domain.entities.LocationShareEvent;
import gather.here.api.domain.repositories.RoomRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

@ActiveProfiles("test")
@SpringBootTest
class RoomRepositoryImplTest {

    @Autowired
    private RoomRepository roomRepository;

    @DisplayName("sut는 LocationShareEvent를 성공적으로 저장한다")
    @Test
    public void saveLocationShareEventTest(){
        //arrange
        Long roomSeq = 1L;
        Long memberSEq = 1L;
        String sessionId = String.valueOf(UUID.randomUUID());
        String nickname = "RoomRepositoryTest";
        String imageUrl = "https://imageUrl.imageurl.com";
        Float presentLat = 1F;
        Float presentLng = 1F;
        Float destinationDistance = 1F;
        LocationShareEvent locationShareEvent = LocationShareEvent.create(
                roomSeq,
                memberSEq,
                sessionId,
                nickname,
                imageUrl,
                presentLat,
                presentLng,
                destinationDistance
        );
        RoomRepository sut = roomRepository;

        //act
        sut.saveLocationShareEvent(locationShareEvent);

        //assert
        LocationShareEvent actual = roomRepository.findLocationShareEventByRoomSeq(roomSeq);
        Assertions.assertThat(actual.getRoomSeq()).isEqualTo(roomSeq);
        Assertions.assertThat(actual.getMemberLocations().get(0).getMemberSeq()).isEqualTo(memberSEq);
        Assertions.assertThat(actual.getScore()).isNull();
    }

    @DisplayName("sut는 score 성공적으로 저장한다")
    @Test
    public void saveScoreTest(){
        //arrange
        Long roomSeq = 1L;
        Long memberSEq = 1L;
        String sessionId = String.valueOf(UUID.randomUUID());
        String nickname = "RoomRepositoryTest";
        String imageUrl = "https://imageUrl.imageurl.com";
        Float presentLat = 1F;
        Float presentLng = 1F;
        Float destinationDistance = 1F;
        LocationShareEvent locationShareEvent = LocationShareEvent.create(
                roomSeq,
                memberSEq,
                sessionId,
                nickname,
                imageUrl,
                presentLat,
                presentLng,
                destinationDistance
        );

        LocationShareEvent.Score score =  new LocationShareEvent.Score();
        score.setGoldMemberSeq(2L);
        locationShareEvent.setScore(score);

        RoomRepository sut = roomRepository;

        //act
        sut.saveLocationShareEvent(locationShareEvent);

        //assert
        LocationShareEvent actual = roomRepository.findLocationShareEventByRoomSeq(roomSeq);
        Assertions.assertThat(actual.getRoomSeq()).isEqualTo(roomSeq);
        Assertions.assertThat(actual.getMemberLocations().get(0).getMemberSeq()).isEqualTo(memberSEq);
        Assertions.assertThat(actual.getScore().getGoldMemberSeq()).isEqualTo(2L);
    }



}