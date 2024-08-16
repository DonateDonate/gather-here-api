package gather.here.api.infra.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AwsS3Config {

    @Bean
    public AmazonS3Client amazonS3Client() {
        String ACCESS_KEY = "AKIAQ3EGWD27KKWFHCY7";
        String SECRET_KEY = "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";

        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withRegion(Regions.AP_NORTHEAST_2).enablePathStyleAccess()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }
}
