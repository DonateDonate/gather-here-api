package gather.here.api.infra.scheduler;


import gather.here.api.domain.entities.LocationShareEvent;
import gather.here.api.domain.entities.Member;
import gather.here.api.domain.entities.Room;
import gather.here.api.domain.entities.WebSocketAuth;
import gather.here.api.domain.repositories.LocationShareEventRepository;
import gather.here.api.domain.repositories.RoomRepository;
import gather.here.api.domain.repositories.WebSocketAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@EnableScheduling
public class RoomScheduler {
    private final RoomRepository roomRepository;
    private final WebSocketAuthRepository webSocketAuthRepository;
    private final LocationShareEventRepository locationShareEventRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void processing() {
        removeRoom();
    }

    private void removeRoom() {
        List<Room> rooms = roomRepository.findAllByStatus(1);
        for (Room room : rooms) {
            if(isPast(room.getEncounterDate().plusDays(1))){
                room.closeRoom();
                Optional<LocationShareEvent> locationShareEvent = locationShareEventRepository.findByRoomSeq(room.getSeq());
                if(locationShareEvent.isPresent()){
                    List<LocationShareEvent.MemberLocation> memberLocations = locationShareEvent.get().getMemberLocations();
                    for(LocationShareEvent.MemberLocation memberLocation : memberLocations){
                        Optional<WebSocketAuth> webSocketAuth = webSocketAuthRepository.findMemberSeq(memberLocation.getMemberSeq());
                        if(webSocketAuth.isPresent()){
                            webSocketAuthRepository.deleteByMemberSeq(webSocketAuth.get());
                        }
                    }
                    locationShareEventRepository.delete(locationShareEvent.get());
                    }

                for(Member member : room.getMemberList()){
                    member.exitRoom();
                }
            }
        }
    }

    private boolean isPast(LocalDateTime encounterDate){
        LocalDateTime nowInKorea = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        return encounterDate.isBefore(nowInKorea);
    }
}
