package gather.here.api.domain.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileFactory {
    String saveFile(MultipartFile multipartFile)throws IOException;
}
