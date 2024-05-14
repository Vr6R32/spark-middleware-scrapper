package com.spark.gateway;

import org.springframework.cloud.gateway.filter.factory.DedupeResponseHeaderGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.route.builder.UriSpec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.socket.server.RequestUpgradeStrategy;
import org.springframework.web.reactive.socket.server.upgrade.TomcatRequestUpgradeStrategy;

import java.util.function.Function;

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
                        .filters(dedupeResponseHeaders())
                        .uri("lb://websocket-service"))

                .route("websocket-service", r -> r.path("/ws/**")
                        .uri("lb:ws://websocket-service"))

                .route("data-service", r -> r.path("/api/v1/currencies/**")
                        .uri("lb://data-service"))

                .route("frontend-service", r -> r.path("/**")
                        .uri("lb://frontend-service"))

                .build();
    }

    private Function<GatewayFilterSpec, UriSpec> dedupeResponseHeaders() {
        String strategy = DedupeResponseHeaderGatewayFilterFactory.Strategy.RETAIN_FIRST.name();
        return f -> f.dedupeResponseHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, strategy)
                .dedupeResponseHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, strategy);
    }
}

