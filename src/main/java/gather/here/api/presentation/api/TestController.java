package gather.here.api.presentation.api;

import gather.here.api.application.service.WebSocketService;
import gather.here.api.domain.entities.LocationShareEvent;
import gather.here.api.domain.entities.WebSocketAuth;
import gather.here.api.domain.service.RoomService;
import gather.here.api.domain.service.dto.request.RoomCreateRequestDto;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class TestController {

    @Autowired
    RoomService roomService;

    @Autowired
    WebSocketService webSocketService;

    @GetMapping("/auth")
    public String auth(){
        return "auth ok";
    }

    @GetMapping("/test/ping")
    public ResponseEntity ping(){
        log.info("test ping!");
        return new ResponseEntity("pong",HttpStatus.OK) ;
    }

    @PostMapping("/test/empty-test")
    public String bodyTest(
            @Valid @RequestBody RoomCreateRequestDto roomCreateRequestDto
            ){
        log.info("roomCreateRequestDto = {}",roomCreateRequestDto);
        return "ok";
    }

    @GetMapping("/test/sessionList")
    public ResponseEntity sessionList(){
        List<WebSocketSession> sessionList = webSocketService.getSessionList();
        List<String> list = new ArrayList<>();
        sessionList.stream().forEach(
                se -> list.add(se.getId())
        );
        return new ResponseEntity(list,HttpStatus.OK);
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
