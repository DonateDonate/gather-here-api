package gather.here.api.domain.service;

import gather.here.api.domain.entities.LocationShareEvent;
import gather.here.api.domain.entities.Member;
import gather.here.api.domain.entities.Room;
import gather.here.api.domain.entities.WebSocketAuth;
import gather.here.api.domain.repositories.LocationShareEventRepository;
import gather.here.api.domain.repositories.MemberRepository;
import gather.here.api.domain.repositories.RoomRepository;
import gather.here.api.domain.repositories.WebSocketAuthRepository;
import gather.here.api.domain.service.dto.request.ExitRoomRequestDto;
import gather.here.api.domain.service.dto.request.JoinRoomRequestDto;
import gather.here.api.domain.service.dto.request.RoomCreateRequestDto;
import gather.here.api.domain.service.dto.response.GetRoomResponseDto;
import gather.here.api.domain.service.dto.response.JoinRoomResponseDto;
import gather.here.api.domain.service.dto.response.RoomCreateResponseDto;
import gather.here.api.global.exception.ResponseStatus;
import gather.here.api.global.exception.RoomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static gather.here.api.global.util.DateUtil.convertLocalDateTimeToString;

@RequiredArgsConstructor
public class RoomService {
    private final MemberRepository memberRepository;
    private final RoomRepository roomRepository;
    private final LocationShareEventRepository locationShareEventRepository;
    private final WebSocketAuthRepository webSocketAuthRepository;

    @Transactional(readOnly = true)
    public GetRoomResponseDto getRoomInfo(Long memberSeq) {
        Member member = memberRepository.getBySeq(memberSeq);
        Room room = member.getRoom();
        if(room != null) {
            return new GetRoomResponseDto(
                    room.getSeq(),
                    room.getDestinationLat(),
                    room.getDestinationLng(),
                    room.getDestinationName(),
                    convertLocalDateTimeToString(room.getEncounterDate()),
                    room.getShareCode()
            );
        }
        return new GetRoomResponseDto();
    }

    @Transactional
    public RoomCreateResponseDto createRoom(RoomCreateRequestDto request, Long memberSeq) {
        Member member = memberRepository.getBySeq(memberSeq);
        if(member.getRoom() != null && member.getRoom().getStatus() == 1){
            throw new RoomException(ResponseStatus.ALREADY_ROOM_ENCOUNTER,HttpStatus.FORBIDDEN);
        }
        Room room = Room.create(
                request.getDestinationLat(),
                request.getDestinationLng(),
                request.getDestinationName(),
                request.getEncounterDate(),
                member);

        roomRepository.save(room);
        member.setRoom(room);

        return new RoomCreateResponseDto(
                room.getSeq(),
                room.getDestinationLat(),
                room.getDestinationLng(),
                room.getDestinationName(),
                convertLocalDateTimeToString(room.getEncounterDate()),
                room.getShareCode()
        );
    }

    @Transactional
    public JoinRoomResponseDto joinRoom(JoinRoomRequestDto request, Long memberSeq) {
        Member member = memberRepository.getBySeq(memberSeq);
        if (member.getRoom() != null && member.getRoom().getStatus() == 1) {
            throw new RoomException(ResponseStatus.ALREADY_ROOM_ENCOUNTER, HttpStatus.FORBIDDEN);
        }
        Room room = roomRepository.getByShareCode(request.getShareCode());

        member.setRoom(room);
        return new JoinRoomResponseDto(
                room.getSeq(),
                room.getDestinationLat(),
                room.getDestinationLng(),
                room.getDestinationName(),
                convertLocalDateTimeToString(room.getEncounterDate()),
                room.getShareCode()
        );
    }

    @Transactional
    public void exitRoom(ExitRoomRequestDto request, Long memberSeq) {
        Member member = memberRepository.getBySeq(memberSeq);

        if (!member.getRoom().getSeq().equals(request.getRoomSeq())) {
            throw new RoomException(ResponseStatus.NOT_FOUND_ROOM_SEQ, HttpStatus.FORBIDDEN);
        }
        LocationShareEvent locationShareEvent = locationShareEventRepository.getByRoomSeq(member.getRoom().getSeq());

        locationShareEvent.removeMemberLocation(member.getSeq());

        if(locationShareEvent.getMemberLocations().isEmpty()){
            locationShareEventRepository.delete(locationShareEvent);
        }else{
            locationShareEventRepository.update(locationShareEvent);
        }

        List<Member> memberList = member.getRoom().getMemberList();
        if (memberList.size() == 1 && memberList.get(0).getSeq().equals(memberSeq)) {
            Room room = member.getRoom();
            room.closeRoom();
        }
        member.exitRoom();
        Optional<WebSocketAuth> webSocketAuth = webSocketAuthRepository.findMemberSeq(memberSeq);
        webSocketAuth.ifPresent(webSocketAuthRepository::deleteByMemberSeq);
    }
}

