package gather.here.api.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberSignInRequestDto {
    @NotNull(message = "필수 항목입니다.")
    @Schema(description = "Member ID (11자리 핸드폰번호 '-' 제외)", example = "01012345678")
    private String identity;

    @NotNull(message = "필수 항목입니다.")
    @Schema(description = "Member 비밀번호(8자리)", example = "12341234")
    private String password;
}
