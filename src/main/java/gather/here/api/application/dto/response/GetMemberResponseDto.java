package gather.here.api.application.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GetMemberResponseDto {
    @Schema(description = "member nickname", example = "행복한 동그랑땡")
    private String nickname;

    @Schema(description = "member id(13자리)", example = "01023341123")
    private String identity;

    @Schema(description = "프로필 이미지 url", example = "http://domain/profile/{imageKey}")
    private String profileImageUrl;
}
