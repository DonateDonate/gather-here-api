package gather.here.api.infra.file.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class DeleteFileRequestDto {
    private String itemKey;
}
