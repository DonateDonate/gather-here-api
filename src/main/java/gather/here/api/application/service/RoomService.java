package gather.here.api.application.service;

import gather.here.api.application.dto.request.RoomCreateRequestDto;
import gather.here.api.application.dto.response.RoomCreateResponseDto;
import gather.here.api.domain.entities.Member;
import gather.here.api.domain.entities.Room;
import gather.here.api.domain.repositories.MemberRepository;
import gather.here.api.domain.repositories.RoomRepository;
import gather.here.api.global.exception.MemberException;
import gather.here.api.global.exception.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

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
                request.getEncounterDate(),
                member);

        roomRepository.save(room);
        return new RoomCreateResponseDto(room.getSeq(),room.getDestinationLat(), room.getDestinationLng(),room.getEncounterDate());
    }

}
