package gather.here.api.infra.file.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
public class S3Provider {
    private final AmazonS3Client amazonS3Client;

    public String uploadFile(MultipartFile multipartFile){
        String imageKey = String.valueOf(UUID.randomUUID());
        File convertFile = new File(imageKey);
        ObjectMetadata metadata = new ObjectMetadata();
        try {
            FileOutputStream fos = new FileOutputStream(convertFile);
            fos.write(multipartFile.getBytes());
            metadata.setContentType(multipartFile.getContentType());
            metadata.setContentLength(multipartFile.getSize());
        }catch (IOException e){
            e.printStackTrace();
        }

        PutObjectRequest putObjectRequest = new PutObjectRequest("gatherhere-bucket", imageKey, convertFile)
                .withMetadata(metadata);
        amazonS3Client.putObject(putObjectRequest);
        convertFile.delete();
       return imageKey;
    }

    public String getImageUrl(String imageKey){
        return amazonS3Client.getUrl("gatherhere-bucket", imageKey).toString();
    }

    public void deleteFile(String imageKey){

    }
}
