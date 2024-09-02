package gather.here.api.domain.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateImageResponseDto {
    @Schema(description = "image url", example = "https://s3.ap-northeast-2.amazonaws.com/gatherhere-bucket/011d2082-e66f-4bae-9e66-90577e6bb157")
    private String imageUrl;
}
