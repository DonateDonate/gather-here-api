package gather.here.api.domain.file;

import org.springframework.web.multipart.MultipartFile;

public interface FileFactory{
     String getImageUrl(String imageKey);
     String uploadFile(MultipartFile multipartFile);
     void deleteFile(String imageKey);
}
