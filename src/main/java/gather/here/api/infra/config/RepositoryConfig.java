package gather.here.api.infra.config;

import gather.here.api.domain.repositories.MemberRepository;
import gather.here.api.domain.repositories.RefreshTokenRepository;
import gather.here.api.domain.repositories.RoomRepository;
import gather.here.api.infra.persistence.MemberRepositoryImpl;
import gather.here.api.infra.persistence.RefreshTokenRepositoryImpl;
import gather.here.api.infra.persistence.RoomRepositoryImpl;
import gather.here.api.infra.persistence.jpa.MemberJpaRepository;
import gather.here.api.infra.persistence.jpa.RefreshTokenJpaRepository;
import gather.here.api.infra.persistence.jpa.RoomJpaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {

    @Bean
    public MemberRepository memberRepository(MemberJpaRepository rep){
        return new MemberRepositoryImpl(rep);
    }

    @Bean
    public RoomRepository roomRepository(RoomJpaRepository rep){
        return new RoomRepositoryImpl(rep);
    }

    @Bean
    public RefreshTokenRepository tokenRepository(RefreshTokenJpaRepository rep){
        return new RefreshTokenRepositoryImpl(rep);

    }
}
