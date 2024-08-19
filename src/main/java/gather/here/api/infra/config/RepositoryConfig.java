package gather.here.api.infra.config;

import gather.here.api.domain.repositories.*;
import gather.here.api.infra.persistence.*;
import gather.here.api.infra.persistence.jpa.AppInfoJpaRepository;
import gather.here.api.infra.persistence.jpa.MemberJpaRepository;
import gather.here.api.infra.persistence.jpa.RefreshTokenJpaRepository;
import gather.here.api.infra.persistence.jpa.RoomJpaRepository;
import gather.here.api.infra.persistence.redis.LocationShareEventRedisRepository;
import gather.here.api.infra.persistence.redis.WebSocketAuthRedisRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {

    @Bean
    public MemberRepository memberRepository(MemberJpaRepository rep){
        return new MemberRepositoryImpl(rep);
    }

    @Bean
    public RoomRepository roomRepository(
            RoomJpaRepository roomJpaRepository,
            LocationShareEventRedisRepository locationShareEventRedisRepository
    ){
        return new RoomRepositoryImpl(roomJpaRepository,locationShareEventRedisRepository);
    }

    @Bean
    public RefreshTokenRepository tokenRepository(RefreshTokenJpaRepository rep){
        return new RefreshTokenRepositoryImpl(rep);
    }

    @Bean
    public AppInfoRepository appInfoRepository(AppInfoJpaRepository rep){
        return new AppInfoRepositoryImpl(rep);
    }

    @Bean
    public WebSocketAuthRepository webSocketAuthRepository(WebSocketAuthRedisRepository rep){
        return new WebSocketAuthRepositoryImpl(rep);
    }
}
