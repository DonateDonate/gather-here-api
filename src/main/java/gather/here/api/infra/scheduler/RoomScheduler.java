package gather.here.api.infra.scheduler;


import gather.here.api.domain.entities.LocationShareEvent;
import gather.here.api.domain.entities.Member;
import gather.here.api.domain.entities.Room;
import gather.here.api.domain.repositories.MemberRepository;
import gather.here.api.domain.repositories.RoomRepository;
import gather.here.api.domain.repositories.WebSocketAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@EnableScheduling
public class RoomScheduler {
    private final RoomRepository roomRepository;
    private final WebSocketAuthRepository webSocketAuthRepository;
    private final MemberRepository memberRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void processing() {
        removeRoom();
    }

    private void removeRoom() {
        List<Room> rooms = roomRepository.findByStatus(1);
        for (Room room : rooms) {
            if(isPast(room.getEncounterDate().plusDays(1))){
                room.closeRoom();
                Optional<LocationShareEvent> locationShareEvent = roomRepository.findLocationShareEventByRoomSeq(room.getSeq());
                if(locationShareEvent.isPresent()){
                    List<LocationShareEvent.MemberLocation> memberLocations = locationShareEvent.get().getMemberLocations();
                    for(LocationShareEvent.MemberLocation memberLocation : memberLocations){
                        webSocketAuthRepository.deleteByMemberSeq(memberLocation.getMemberSeq());
                    }
                    roomRepository.deleteLocationShareEvent(locationShareEvent.get());
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
