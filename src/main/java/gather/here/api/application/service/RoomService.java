package gather.here.api.application.service;

import gather.here.api.application.dto.request.ExitRoomRequestDto;
import gather.here.api.application.dto.request.JoinRoomRequestDto;
import gather.here.api.application.dto.request.LocationShareEventRequestDto;
import gather.here.api.application.dto.request.RoomCreateRequestDto;
import gather.here.api.application.dto.response.JoinRoomResponseDto;
import gather.here.api.application.dto.response.RoomCreateResponseDto;
import gather.here.api.domain.entities.LocationShareEvent;
import gather.here.api.domain.entities.Member;
import gather.here.api.domain.entities.Room;
import gather.here.api.domain.entities.WebSocketAuth;
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

import static gather.here.api.global.util.DateUtil.convertLocalDateTimeToString;

@RequiredArgsConstructor
public class RoomService {
    private final MemberRepository memberRepository;
    private final RoomRepository roomRepository;
    private final WebSocketAuthRepository webSocketAuthRepository;

    @Transactional
    public RoomCreateResponseDto createRoom(RoomCreateRequestDto request, String memberIdentity){
        Member member = memberRepository.findByIdentity(memberIdentity).orElseThrow(
                () -> new MemberException(ResponseStatus.INVALID_IDENTITY_PASSWORD, HttpStatus.BAD_REQUEST));

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

        //todo add redis create logic
    }

    @Transactional
    public JoinRoomResponseDto joinRoom(JoinRoomRequestDto request, String memberIdentity){
        Member member = memberRepository.findByIdentity(memberIdentity).orElseThrow(
                ()-> new MemberException(ResponseStatus.INVALID_IDENTITY_PASSWORD,HttpStatus.BAD_REQUEST)
        );

        if(member.getRoom() != null){
            throw new RoomException(ResponseStatus.ALREADY_ROOM_ENCOUNTER,HttpStatus.CONFLICT);
        }

        Room room = roomRepository.findByShareCode(request.getShareCode()).orElseThrow(
                ()-> new RoomException(ResponseStatus.NOT_FOUND_SHARE_CODE,HttpStatus.UNAUTHORIZED));

        if(room.getStatus() != 1){
            throw new RoomException(ResponseStatus.CLOSED_ROOM,HttpStatus.CONFLICT);
        }

        room.addMemberList(member);
        return new JoinRoomResponseDto(
                room.getSeq(),
                room.getDestinationLat(),
                room.getDestinationLng(),
                room.getDestinationName(),
                room.getEncounterDate(),
                room.getShareCode()
        );

        //todo add join redis room logic
    }

    @Transactional
    public void exitRoom(ExitRoomRequestDto request, String memberIdentity){
        Member member = memberRepository.findByIdentity(memberIdentity).orElseThrow(
                ()-> new MemberException(ResponseStatus.INVALID_IDENTITY_PASSWORD,HttpStatus.BAD_REQUEST)
        );

        if(member.getRoom().getSeq() != request.getRoomSeq()){
            throw new RoomException(ResponseStatus.NOT_FOUND_ROOM_SEQ,HttpStatus.BAD_REQUEST);
        }

        member.setRoom(null);
    }

    @Transactional
    public void createLocationShareEvent(LocationShareEventRequestDto request, String memberIdentity, String memberSessionId){
        Member member = memberRepository.findByIdentity(memberIdentity).orElseThrow(
                ()-> new MemberException(ResponseStatus.INVALID_IDENTITY_PASSWORD,HttpStatus.BAD_REQUEST)
        );

        //member session id는 redis에서 가져오기 -> 최초에 연결이 되면 memberSession를 넣어줘야함

        Room room = member.getRoom();

        LocationShareEvent locationShareEvent = LocationShareEvent.create(
                room.getSeq(),
                member.getSeq(), memberSessionId,
                member.getNickname(),
                member.getImageKey(),
                request.getPresentLat(),
                request.getPresentLng(),
                request.getDestinationDistance()
        );
        roomRepository.createLocationShareEvent(locationShareEvent);
    }

    @Transactional
    public void saveWebSocketAuth(WebSocketAuth webSocketAuth){
        webSocketAuthRepository.save(webSocketAuth);
    }

    public List<WebSocketAuth> findByAllWebSocketAuth(){
        return webSocketAuthRepository.findAll();
    }

}
