package gather.here.api.infra.config;

import gather.here.api.application.service.WebSocketService;
import gather.here.api.infra.socket.CustomWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    private final WebSocketService webSocketService;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(customWebSocketHandler(webSocketService),"/location/share")
                .setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler customWebSocketHandler(WebSocketService webSocketService){
        return new CustomWebSocketHandler(webSocketService);
    }

    @Bean
    public ServletServerContainerFactoryBean servletServerContainerFactoryBean(){
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxBinaryMessageBufferSize(8192);
        container.setMaxTextMessageBufferSize(8192);
        container.setMaxSessionIdleTimeout(600000L);
        return container;
    }
}