package gather.here.api.presentation.api;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Room API", description = "Room API Docs")
public class RoomController {
    private final RoomService roomService;

    @PostMapping("/rooms")
    @Operation(summary = "Room 등록", description = "Room 등록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = {@Content(schema = @Schema(implementation = RoomCreateResponseDto.class))})
    })
    public ResponseEntity<Object> create(
            @Valid @RequestBody RoomCreateRequestDto request,
            Authentication authentication
    ){
        RoomCreateResponseDto response = roomService.createRoom(request, String.valueOf(authentication.getPrincipal()));
        return new ResponseEntity<Object>(response,HttpStatus.OK);
    }

    @PostMapping("/rooms/join")
    @Operation(summary = "Room 참여", description = "Room 참여")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = {@Content(schema = @Schema(implementation = JoinRoomResponseDto.class))})
    })
    public ResponseEntity<Object> join(
            @Valid @RequestBody JoinRoomRequestDto request,
            Authentication authentication
    ){
        JoinRoomResponseDto response = roomService.joinRoom(request, String.valueOf(authentication.getPrincipal()));
        return new ResponseEntity<Object>(response,HttpStatus.OK);
    }
}
