package gather.here.api.infra.config;

import gather.here.api.application.service.WebSocketService;
import gather.here.api.domain.file.FileFactory;
import gather.here.api.domain.repositories.*;
import gather.here.api.domain.security.AccessTokenFactory;
import gather.here.api.domain.security.CryptoFactory;
import gather.here.api.domain.security.RefreshTokenFactory;
import gather.here.api.domain.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
            LocationShareEventRepository locationShareEventRepository,
            WebSocketAuthRepository webSocketAuthRepository
    ){
        return new RoomService(memberRepository,roomRepository,locationShareEventRepository,webSocketAuthRepository);
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
    public LocationShareService locationShareService (
            WebSocketAuthRepository webSocketAuthRepository,
            MemberRepository memberRepository,
            FileFactory fileFactory,
            LocationShareEventRepository locationShareEventRepository
    ){
        return new LocationShareService(webSocketAuthRepository,memberRepository,fileFactory,locationShareEventRepository);
    }

    @Bean
    public WebSocketService webSocketService(TokenService tokenService, LocationShareService locationShareService){
        return new WebSocketService(tokenService, locationShareService);
    }
}
