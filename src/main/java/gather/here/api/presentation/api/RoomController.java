package gather.here.api.presentation.api;

import gather.here.api.application.dto.request.ExitRoomRequestDto;
import gather.here.api.application.dto.request.JoinRoomRequestDto;
import gather.here.api.application.dto.request.RoomCreateRequestDto;
import gather.here.api.application.dto.response.JoinRoomResponseDto;
import gather.here.api.application.dto.response.RoomCreateResponseDto;
import gather.here.api.application.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Room API", description = "Room API Docs")
public class RoomController {
    private static final Logger log = LoggerFactory.getLogger(RoomController.class);
    private final RoomService roomService;

    @PostMapping("/rooms")
    @Operation(summary = "Room 등록", description = "Room 등록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = {@Content(schema = @Schema(implementation = RoomCreateResponseDto.class))})
    })
    public ResponseEntity<RoomCreateResponseDto> create(
            @Valid @RequestBody RoomCreateRequestDto request,
            Authentication authentication
    ){
        RoomCreateResponseDto response = roomService.createRoom(request, String.valueOf(authentication.getPrincipal()));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/rooms/join")
    @Operation(summary = "Room 참여", description = "Room 참여")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = {@Content(schema = @Schema(implementation = JoinRoomResponseDto.class))})
    })
    public ResponseEntity<JoinRoomResponseDto> join(
            @Valid @RequestBody JoinRoomRequestDto request,
            Authentication authentication
    ){
        JoinRoomResponseDto response = roomService.joinRoom(request, String.valueOf(authentication.getPrincipal()));
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/rooms/exit")
    @Operation(summary = "Room 나가기", description = "Room 나가기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = {@Content(schema = @Schema(implementation = void.class))})
    })
    public ResponseEntity<Object> exit(
            @Valid @RequestBody ExitRoomRequestDto request,
            Authentication authentication
    ){
        roomService.exitRoom(request,String.valueOf(authentication.getPrincipal()));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/location/share")
    public ResponseEntity<Object> locationShareConnect(){
        log.info("location share!");
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
