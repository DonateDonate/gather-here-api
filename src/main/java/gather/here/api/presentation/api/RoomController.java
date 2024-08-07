package gather.here.api.presentation.api;

import gather.here.api.application.dto.request.RoomCreateRequestDto;
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
    public ResponseEntity create(
            RoomCreateRequestDto request,
            Authentication authentication
    ){
        roomService.createRoom(request, String.valueOf(authentication.getPrincipal()));
        return new ResponseEntity(HttpStatus.OK);
    }
}
