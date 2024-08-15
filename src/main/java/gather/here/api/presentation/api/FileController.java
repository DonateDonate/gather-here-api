package gather.here.api.presentation.api;

import gather.here.api.domain.file.FileFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class FileController {
    private final FileFactory fileFactory;

    @PostMapping(value = "/file/profile",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void uploadFile(@RequestPart MultipartFile file, Authentication authentication){
        try {
            fileFactory.saveFile(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
