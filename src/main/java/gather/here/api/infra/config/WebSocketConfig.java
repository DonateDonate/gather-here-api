package gather.here.api.infra.config;

import gather.here.api.domain.service.LocationShareService;
import gather.here.api.domain.service.TokenService;
import gather.here.api.infra.socket.CustomWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    private final TokenService tokenService;
    private final LocationShareService locationShareService;

    @Bean
    public WebSocketHandler customWebSocketHandler(TokenService tokenService,LocationShareService locationShareService){
        return new CustomWebSocketHandler(tokenService,locationShareService);
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(customWebSocketHandler(tokenService,locationShareService),"/location/share")
                .setAllowedOrigins("*");
    }
}