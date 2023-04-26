package ch.uzh.ifi.hase.soprafs23.service;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebScoketConfig implements WebSocketMessageBrokerConfigurer{
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config){
        config.setApplicationDestinationPrefixes("/app");
        config.enableSimpleBroker("/instance");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registery){
        registery.addEndpoint("socket")
                .setAllowedOriginPatterns("http://localhost:3000","https://sopra-fs23-group-32-client.oa.r.appspot.com")
                .withSockJS();
    }
}
