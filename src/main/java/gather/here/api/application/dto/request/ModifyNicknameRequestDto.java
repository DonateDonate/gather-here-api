package gather.here.api.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(title = "member nickname modify dto")
@Getter
@NoArgsConstructor
public class ModifyNicknameRequestDto {
    @Schema(description = "Member 새로운 닉네임", example = "배고파종마루")
    private String nickname;
}
