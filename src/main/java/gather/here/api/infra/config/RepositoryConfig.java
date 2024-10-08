package gather.here.api.infra.config;

import gather.here.api.domain.repositories.*;
import gather.here.api.infra.persistence.*;
import gather.here.api.infra.persistence.jpa.AppInfoJpaRepository;
import gather.here.api.infra.persistence.jpa.MemberJpaRepository;
import gather.here.api.infra.persistence.jpa.RefreshTokenJpaRepository;
import gather.here.api.infra.persistence.jpa.RoomJpaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RepositoryConfig {

    @Bean
    public MemberRepository memberRepository(MemberJpaRepository rep){
        return new MemberRepositoryImpl(rep);
    }

    @Bean
    public RoomRepository roomRepository(
            RoomJpaRepository roomJpaRepository
    ){
        return new RoomRepositoryImpl(roomJpaRepository);
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
    public WebSocketAuthRepository webSocketAuthRepository(RedisOperations<String, Object> redisOperations){
        return new WebSocketAuthRedisTemplateRepositoryImpl(redisOperations);
    }

    @Bean
    public LocationShareEventRepository locationShareEventRepository(RedisTemplate<String, Object> redisTemplate){
        return new LocationShareEventRedisTemplateRepositoryImpl(redisTemplate);
    }
}
