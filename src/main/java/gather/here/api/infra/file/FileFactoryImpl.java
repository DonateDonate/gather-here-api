package gather.here.api.infra.file;

import gather.here.api.domain.file.FileFactory;
import gather.here.api.infra.file.s3.S3Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
public class FileFactoryImpl implements FileFactory {

    private final S3Provider s3Provider;
    @Override
    public String uploadFile(MultipartFile multipartFile) {
        return s3Provider.uploadFile(multipartFile);
    }

    @Override
    public String getImageUrl(String imageKey) {
        return s3Provider.getImageUrl(imageKey);
    }

    @Override
    public void deleteFile(String imageKey) {
        s3Provider.deleteFile(imageKey);
    }
}
