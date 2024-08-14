package gather.here.api.presentation.api;

import gather.here.api.application.dto.request.RoomCreateRequestDto;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class TestController {
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
}
