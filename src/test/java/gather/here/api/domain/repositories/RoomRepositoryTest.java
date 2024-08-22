//package gather.here.api.domain.repositories;
//
//import gather.here.api.domain.entities.LocationShareEvent;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@ActiveProfiles("devel")
//class RoomRepositoryTest {
//
//    @Autowired
//    private RoomRepository roomRepository;
//
//
//    @Test
//    public void test(){
//        Long memberSeq = 2L;
//        LocationShareEvent locationShareEventByRoomSeq = roomRepository.findLocationShareEventByRoomSeq(1L);
//        Assertions.assertThat(locationShareEventByRoomSeq).isNotNull();
//    }
//
//}