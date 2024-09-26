package gather.here.api.infra.file;

import gather.here.api.application.service.dto.request.ShItemUploadFileRequestDto;
import gather.here.api.domain.file.FileFactory;
import gather.here.api.infra.file.dto.GetImageUrlResponseDto;
import gather.here.api.infra.file.dto.UploadFileResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

public class ShFileFactoryImpl implements FileFactory {

    @Value("${sh.item.server.url}")
    public String SH_ITEM_SERVER_URL;

    @Value("${sh.item.server.clientId}")
    public String SH_ITEM_SERVER_CLIENT_ID;

    @Value("${sh.item.server.secretKey}")
    public String SH_ITEM_SERVER_SECRET_KEY;

    @Override
    public String getImageUrl(String imageKey) {
        if(imageKey == null){
            return "";
        }

        GetImageUrlResponseDto response = WebClient.create(SH_ITEM_SERVER_URL + "/" + imageKey)
                .get()
                .header("clientId", SH_ITEM_SERVER_CLIENT_ID)
                .header("secretKey", SH_ITEM_SERVER_SECRET_KEY)
                .retrieve()
                .bodyToMono(GetImageUrlResponseDto.class)
                .block();
        return response.getItemUrl();
    }

    @Override
    public String uploadFile(MultipartFile multipartFile) {

        String imageKey = String.valueOf(UUID.randomUUID());

        UploadFileResponseDto response = WebClient.create(SH_ITEM_SERVER_URL)
                .post()
                .header("clientId", SH_ITEM_SERVER_CLIENT_ID)
                .header("secretKey", SH_ITEM_SERVER_SECRET_KEY)
                .body(BodyInserters.fromMultipartData("item", multipartFile.getResource())
                        .with("saveItemRequestDto", new ShItemUploadFileRequestDto(imageKey))
                )
                .retrieve()
                .bodyToMono(UploadFileResponseDto.class)
                .block();
        return response.getItemKey();
    }

    @Override
    public void deleteFile(String imageKey) {
        WebClient.create(SH_ITEM_SERVER_URL + "/" + imageKey)
                .delete()
                .header("clientId", SH_ITEM_SERVER_CLIENT_ID)
                .header("secretKey", SH_ITEM_SERVER_SECRET_KEY)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
