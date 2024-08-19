package gather.here.api.infra.config;

import gather.here.api.application.service.RoomService;
import gather.here.api.application.service.TokenService;
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
    private final RoomService roomService;
    private final TokenService tokenService;

    @Bean
    public WebSocketHandler customWebSocketHandler(RoomService roomService,TokenService tokenService){
        return new CustomWebSocketHandler(roomService,tokenService);
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        System.out.println("registry = " + registry);
        registry.addHandler(customWebSocketHandler(roomService,tokenService),"/location/share")
                .setAllowedOrigins("*");
    }
}