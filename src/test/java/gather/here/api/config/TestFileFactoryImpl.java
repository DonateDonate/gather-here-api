package gather.here.api.config;

import gather.here.api.domain.file.FileFactory;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public class TestFileFactoryImpl implements FileFactory {
    @Override
    public String getImageUrl(String imageKey) {
        return "http://test/test.com";
    }

    @Override
    public String uploadFile(MultipartFile multipartFile) {
        return String.valueOf(UUID.randomUUID());
    }

    @Override
    public void deleteFile(String imageKey) {

    }
}
