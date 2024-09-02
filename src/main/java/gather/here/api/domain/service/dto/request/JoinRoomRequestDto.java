package gather.here.api.domain.service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(title = "room join dto")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class JoinRoomRequestDto {

    @NotNull(message = "필수 항목입니다.")
    @Schema(description = "공유 코드(4자리 숫자,영어)", example = "AB14")
    private String shareCode;
}
