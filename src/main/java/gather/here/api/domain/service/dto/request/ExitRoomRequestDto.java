package gather.here.api.domain.service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Schema(title = "room exit dto")
@Getter
public class ExitRoomRequestDto {

    @NotNull(message = "필수 항목입니다.")
    @Schema(description = "Room 식별값", example = "1")
    private Long roomSeq;
}
