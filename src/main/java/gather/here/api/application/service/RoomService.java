package gather.here.api.application.service;

import gather.here.api.application.dto.request.ExitRoomRequestDto;
import gather.here.api.application.dto.request.JoinRoomRequestDto;
import gather.here.api.application.dto.request.RoomCreateRequestDto;
import gather.here.api.application.dto.response.JoinRoomResponseDto;
import gather.here.api.application.dto.response.RoomCreateResponseDto;
import gather.here.api.domain.entities.Member;
import gather.here.api.domain.entities.Room;
import gather.here.api.domain.repositories.MemberRepository;
import gather.here.api.domain.repositories.RoomRepository;
import gather.here.api.global.exception.MemberException;
import gather.here.api.global.exception.ResponseStatus;
import gather.here.api.global.exception.RoomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import static gather.here.api.global.util.DateUtil.convertLocalDateTimeToString;

@RequiredArgsConstructor
public class RoomService {
    private final MemberRepository memberRepository;
    private final RoomRepository roomRepository;

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
        return new JoinRoomResponseDto(room.getSeq(),room.getDestinationLat(),room.getDestinationLng(),room.getEncounterDate());
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




}
