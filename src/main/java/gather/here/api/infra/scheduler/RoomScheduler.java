package gather.here.api.infra.scheduler;


import gather.here.api.domain.entities.LocationShareEvent;
import gather.here.api.domain.entities.Room;
import gather.here.api.domain.repositories.RoomRepository;
import gather.here.api.domain.repositories.WebSocketAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@RequiredArgsConstructor
@EnableScheduling
public class RoomScheduler {
    private final RoomRepository roomRepository;
    private final WebSocketAuthRepository webSocketAuthRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void removeRoom() {
        List<Room> rooms = roomRepository.findByStatus(1);
        for (Room room : rooms) {
            if(isPast(room.getEncounterDate().plusHours(1L))){
                room.closeRoom();
                LocationShareEvent locationShareEvent = roomRepository.getLocationShareEventByRoomSeq(room.getSeq());
                List<LocationShareEvent.MemberLocation> memberLocations = locationShareEvent.getMemberLocations();
                for(LocationShareEvent.MemberLocation memberLocation : memberLocations){
                    webSocketAuthRepository.deleteByMemberSeq(memberLocation.getMemberSeq());
                }
                roomRepository.deleteLocationShareEvent(locationShareEvent);
            }
        }
    }

    private boolean isPast(LocalDateTime encounterDate){
        LocalDateTime nowInKorea = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        return encounterDate.isBefore(nowInKorea);
    }
}
