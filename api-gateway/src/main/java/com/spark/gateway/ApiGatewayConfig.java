package com.spark.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.socket.server.RequestUpgradeStrategy;
import org.springframework.web.reactive.socket.server.upgrade.TomcatRequestUpgradeStrategy;

@Configuration
public class ApiGatewayConfig {

    // TODO IN FUTURE IMPLEMENT SECURITY FOR WHOLE INFRASTRUCTURE

    @Bean
    @Primary
    public RequestUpgradeStrategy requestUpgradeStrategy() {
        return new TomcatRequestUpgradeStrategy();
    }

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()

                .route("websocket-service-handshake", r -> r.path("/websocket/**")
                        .uri("lb://websocket-service"))

                .route("websocket-service", r -> r.path("/ws/**")
                        .uri("lb:ws://websocket-service"))

                .route("data-service", r -> r.path("/api/v1/currencies/**")
                        .uri("lb://data-service"))

                .route("frontend-service", r -> r.path("/**")
                        .uri("lb://frontend-service"))

                .build();
    }
}

