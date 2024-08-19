package gather.here.api.infra.config;

import com.amazonaws.services.s3.AmazonS3Client;
import gather.here.api.application.service.*;
import gather.here.api.domain.file.FileFactory;
import gather.here.api.domain.repositories.*;
import gather.here.api.domain.security.AccessTokenFactory;
import gather.here.api.domain.security.CryptoFactory;
import gather.here.api.domain.security.RefreshTokenFactory;
import gather.here.api.infra.crypto.CryptoFactoryImpl;
import gather.here.api.infra.file.FileFactoryImpl;
import gather.here.api.infra.file.s3.S3Provider;
import gather.here.api.infra.security.AccessTokenFactoryImpl;
import gather.here.api.infra.security.RefreshTokenFactoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ServiceConfig {

    @Bean
    public AppInfoService appInfoService(AppInfoRepository appInfoRepository){
        return new AppInfoService(appInfoRepository);
    }

    @Bean
    public RoomService roomService(
            MemberRepository memberRepository,
        RoomRepository roomRepository,
            WebSocketAuthRepository webSocketAuthRepository
    ){
        return new RoomService(memberRepository,roomRepository,webSocketAuthRepository);
    }

    @Bean
    public FileService fileService(){
        return new FileService();
    }

    @Bean
    public MemberService memberService(
            MemberRepository memberRepository,
            CryptoFactory cryptoFactory,
            FileFactory fileFactory
            ){
        return new MemberService(memberRepository,cryptoFactory,fileFactory);
    }

    @Bean
    public TokenService tokenService(
            AccessTokenFactory accessTokenFactory,
            RefreshTokenFactory refreshTokenFactory,
            MemberRepository memberRepository
    ){
        return new TokenService(accessTokenFactory, refreshTokenFactory,memberRepository);
    }

    @Bean
    public CryptoFactory cryptoFactory(PasswordEncoder passwordEncoder){
        return new CryptoFactoryImpl(passwordEncoder);
    }

    @Bean
    public FileFactory fileFactory(S3Provider s3Provider){
        return new FileFactoryImpl(s3Provider);
    }

    @Bean
    public AccessTokenFactory accessTokenFactory(){
        return new AccessTokenFactoryImpl();
    }


    @Bean
    public RefreshTokenFactory refreshTokenFactory(RefreshTokenRepository repository){
        return new RefreshTokenFactoryImpl(repository);
    }

    @Bean
    public S3Provider s3Provider(AmazonS3Client amazonS3Client){
        return new S3Provider(amazonS3Client);
    }

}
