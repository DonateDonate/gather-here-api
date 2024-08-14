package gather.here.api.presentation.api;

import gather.here.api.application.dto.request.RoomCreateRequestDto;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/test")
@RestController
public class TestController {

    @GetMapping("/ping")
    public String ping(){
        return "pong";
    }

    @PostMapping("/empty-test")
    public String bodyTest(
            @Valid @RequestBody RoomCreateRequestDto roomCreateRequestDto
            ){
        log.info("roomCreateRequestDto = {}",roomCreateRequestDto);
        return "ok";
    }
}
