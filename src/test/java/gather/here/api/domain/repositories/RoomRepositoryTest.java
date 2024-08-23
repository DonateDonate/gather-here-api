package gather.here.api.domain.repositories;

import gather.here.api.domain.entities.LocationShareEvent;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
class RoomRepositoryTest {

    @Autowired
    private RoomRepository roomRepository;

    @Test
    public void saveTest(){
        Long roomSeq = 7L;
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
        roomRepository.saveLocationShareEvent(locationShareEvent);
        LocationShareEvent locationShareEventByRoomSeq = roomRepository.findLocationShareEventByRoomSeq(roomSeq);
        Assertions.assertThat(locationShareEventByRoomSeq).isNotNull();
    }
}