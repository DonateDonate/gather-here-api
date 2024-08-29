package gather.here.api.application.service;

import gather.here.api.application.dto.request.ExitRoomRequestDto;
import gather.here.api.application.dto.request.JoinRoomRequestDto;
import gather.here.api.application.dto.request.RoomCreateRequestDto;
import gather.here.api.application.dto.response.JoinRoomResponseDto;
import gather.here.api.application.dto.response.RoomCreateResponseDto;
import gather.here.api.domain.entities.LocationShareEvent;
import gather.here.api.domain.entities.Member;
import gather.here.api.domain.entities.Room;
import gather.here.api.domain.entities.WebSocketAuth;
import gather.here.api.domain.file.FileFactory;
import gather.here.api.domain.repositories.MemberRepository;
import gather.here.api.domain.repositories.RoomRepository;
import gather.here.api.domain.repositories.WebSocketAuthRepository;
import gather.here.api.global.exception.MemberException;
import gather.here.api.global.exception.ResponseStatus;
import gather.here.api.global.exception.RoomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static gather.here.api.global.util.DateUtil.convertLocalDateTimeToString;

@RequiredArgsConstructor
public class RoomService {
    private final MemberRepository memberRepository;
    private final RoomRepository roomRepository;
    private final WebSocketAuthRepository webSocketAuthRepository;
    private final FileFactory fileFactory;

    @Transactional
    public RoomCreateResponseDto createRoom(RoomCreateRequestDto request, Long memberSeq){
        Member member = memberRepository.findBySeq(memberSeq).orElseThrow(
                ()-> new MemberException(ResponseStatus.NOT_FOUND_MEMBER,HttpStatus.BAD_REQUEST)
        );
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
    public JoinRoomResponseDto joinRoom(JoinRoomRequestDto request, Long memberSeq){
        Member member = memberRepository.findBySeq(memberSeq).orElseThrow(
                ()-> new MemberException(ResponseStatus.NOT_FOUND_MEMBER,HttpStatus.BAD_REQUEST)
        );
        if(member.getRoom() != null){
            throw new RoomException(ResponseStatus.ALREADY_ROOM_ENCOUNTER,HttpStatus.CONFLICT);
        }

        Room room = roomRepository.findByShareCode(request.getShareCode()).orElseThrow(
                ()-> new RoomException(ResponseStatus.NOT_FOUND_SHARE_CODE,HttpStatus.UNAUTHORIZED));

        if(room.getStatus() != 1){
            throw new RoomException(ResponseStatus.CLOSED_ROOM,HttpStatus.CONFLICT);
        }

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
    public void exitRoom(ExitRoomRequestDto request, Long memberSeq){
        Member member = memberRepository.findBySeq(memberSeq).orElseThrow(
                ()-> new MemberException(ResponseStatus.NOT_FOUND_MEMBER,HttpStatus.BAD_REQUEST)
        );

        if(member.getRoom().getSeq() != request.getRoomSeq()){
            throw new RoomException(ResponseStatus.NOT_FOUND_ROOM_SEQ,HttpStatus.BAD_REQUEST);
        }

        member.setRoom(null);
    }

    public List<WebSocketAuth> findByAllWebSocketAuth(){
        return webSocketAuthRepository.findAll();
    }

    public List<LocationShareEvent> findByAllLocationShareEvent() {
        Iterable<LocationShareEvent> allLocationEvents = roomRepository.findAllLocationEvents();
        return StreamSupport.stream(allLocationEvents.spliterator(), false)
                .collect(Collectors.toList());
    }
}
