package gather.here.api.application.service;

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
        Room room = roomRepository.findByShareCode(request.getShareCode()).orElseThrow(
                ()-> new RoomException(ResponseStatus.NOT_FOUND_SHARE_CODE,HttpStatus.UNAUTHORIZED));
        //isActive가 false이면 못 들어감
        Member member = memberRepository.findByIdentity(memberIdentity).orElseThrow(
                ()-> new MemberException(ResponseStatus.INVALID_INPUT,HttpStatus.BAD_REQUEST)
        );
        room.addMemberList(member);
        return new JoinRoomResponseDto(room.getSeq(),room.getDestinationLat(),room.getDestinationLng(),room.getEncounterDate());
    }

}
