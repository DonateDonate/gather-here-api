package gather.here.api.presentation.api;

import gather.here.api.application.dto.request.JoinRoomRequestDto;
import gather.here.api.application.dto.request.RoomCreateRequestDto;
import gather.here.api.application.dto.response.JoinRoomResponseDto;
import gather.here.api.application.dto.response.RoomCreateResponseDto;
import gather.here.api.application.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @PostMapping("/rooms")
    public ResponseEntity<Object> create(
            RoomCreateRequestDto request,
            Authentication authentication
    ){
        RoomCreateResponseDto response = roomService.createRoom(request, String.valueOf(authentication.getPrincipal()));
        return new ResponseEntity<Object>(response,HttpStatus.OK);
    }

    @PostMapping("/rooms/join")
    public ResponseEntity<Object> join(
            JoinRoomRequestDto request,
            Authentication authentication
    ){
        JoinRoomResponseDto response = roomService.joinRoom(request, String.valueOf(authentication.getPrincipal()));
        return new ResponseEntity<Object>(response,HttpStatus.OK);
    }
}
