package gather.here.api.presentation.api;

import gather.here.api.application.service.WebSocketService;
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
}
