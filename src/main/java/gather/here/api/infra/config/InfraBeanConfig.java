package gather.here.api.infra.config;

import gather.here.api.domain.repositories.*;
import gather.here.api.domain.security.AccessTokenFactory;
import gather.here.api.domain.security.CryptoFactory;
import gather.here.api.domain.security.RefreshTokenFactory;
import gather.here.api.infra.crypto.CryptoFactoryImpl;
import gather.here.api.infra.file.ShFileFactoryImpl;
import gather.here.api.infra.scheduler.RoomScheduler;
import gather.here.api.infra.security.AccessTokenFactoryImpl;
import gather.here.api.infra.security.RefreshTokenFactoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class InfraBeanConfig {

    @Bean
    public CryptoFactory cryptoFactory(PasswordEncoder passwordEncoder) {
        return new CryptoFactoryImpl(passwordEncoder);
    }

    @Bean
    public ShFileFactoryImpl fileFactory() {
        return new ShFileFactoryImpl();
    }

    @Bean
    public AccessTokenFactory accessTokenFactory() {
        return new AccessTokenFactoryImpl();
    }

    @Bean
    public RefreshTokenFactory refreshTokenFactory(RefreshTokenRepository repository ){
        return new RefreshTokenFactoryImpl(repository);
    }

    @Bean
    public RoomScheduler roomScheduler(RoomRepository roomRepository, WebSocketAuthRepository webSocketAuthRepository, LocationShareEventRepository locationShareEventRepository) {
        return new RoomScheduler(roomRepository,webSocketAuthRepository,locationShareEventRepository);
    }

}
