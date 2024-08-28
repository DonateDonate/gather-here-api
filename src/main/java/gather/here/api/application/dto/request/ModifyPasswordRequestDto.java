package gather.here.api.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(title = "Member password modify dto")
@Getter
@NoArgsConstructor
public class ModifyPasswordRequestDto {
    @Schema(description = "Member 새로운 비밀번호 ", example = "12341234")
    private String password;
}
