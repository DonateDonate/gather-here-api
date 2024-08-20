package gather.here.api.presentation.api;

import gather.here.api.application.dto.request.RoomCreateRequestDto;
import gather.here.api.application.service.RoomService;
import gather.here.api.domain.entities.LocationShareEvent;
import gather.here.api.domain.entities.WebSocketAuth;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class TestController {

    @Autowired
    RoomService roomService;

    @GetMapping("/auth")
    public String auth(){
        return "auth ok";
    }

    @GetMapping("/test/ping")
    public String ping(){
        return "pong";
    }

    @PostMapping("/test/empty-test")
    public String bodyTest(
            @Valid @RequestBody RoomCreateRequestDto roomCreateRequestDto
            ){
        log.info("roomCreateRequestDto = {}",roomCreateRequestDto);
        return "ok";
    }

    @GetMapping("/test/webSocketAuth")
    public ResponseEntity getWebSocketAuthList(){
        List<WebSocketAuth> byAllWebSocketAuth = roomService.findByAllWebSocketAuth();
        return new ResponseEntity(byAllWebSocketAuth, HttpStatus.OK);
    }

    @GetMapping("/test/locationShareEvent")
    public ResponseEntity getLocationShareEvent(){
        List<LocationShareEvent> locationShareEvents = roomService.findByAllLocationShareEvent();
        return new ResponseEntity(locationShareEvents, HttpStatus.OK);
    }
}
