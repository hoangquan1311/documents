package com.example.demo.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:3000")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic");
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(2 * 1024 * 1024);  // Giới hạn kích thước tin nhắn
        registration.setSendBufferSizeLimit(2 * 1024 * 1024); // Giới hạn kích thước bộ đệm gửi
        registration.setSendTimeLimit(20000);  // Giới hạn thời gian gửi tin nhắn
    }

//    @Bean
//    public ServletServerContainerFactoryBean createWebSocketContainer() {
//        ServletServerContainerFactoryBean factoryBean = new ServletServerContainerFactoryBean();
//        factoryBean.setMaxBinaryMessageBufferSize(2 * 1024 * 1024);  // Giới hạn kích thước bộ đệm nhị phân
//        factoryBean.setMaxTextMessageBufferSize(2 * 1024 * 1024);    // Giới hạn kích thước bộ đệm văn bản
//        factoryBean.setMaxSessionIdleTimeout(60 * 1000L);            // Giới hạn thời gian timeout của phiên
//        return factoryBean;
//    }
}
