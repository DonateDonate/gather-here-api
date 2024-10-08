package gather.here.api.domain.service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(title = "Member password modify dto")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ModifyPasswordRequestDto {
    @Schema(description = "Member 새로운 비밀번호 ", example = "12341234")
    private String password;
}
