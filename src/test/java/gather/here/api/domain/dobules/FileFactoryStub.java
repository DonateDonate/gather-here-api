package gather.here.api.domain.dobules;

import gather.here.api.domain.file.FileFactory;
import org.springframework.web.multipart.MultipartFile;

public class FileFactoryStub implements FileFactory {
    @Override
    public String getImageUrl(String imageKey) {
        if(imageKey == null) {
            throw new RuntimeException();
        }

        return imageKey;
    }

    @Override
    public String uploadFile(MultipartFile multipartFile) {
        return "";
    }

    @Override
    public void deleteFile(String imageKey) {

    }
}
