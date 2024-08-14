package gather.here.api.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ModifyPasswordRequestDto {
    @Schema(description = "Member 새로운 비밀번호 ", example = "12!123")
    private String password;
}
