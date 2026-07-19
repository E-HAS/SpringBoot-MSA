package com.msa.gateway.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {

    public GlobalFilter(){
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            String id = request.getId();

            log.info("Global Filter baseMessage : {}", config.getBaseMessage());

            if (!config.isPreLogger()) {
                return chain.filter(exchange)
                		.then(Mono.fromRunnable(() -> { 
               			 if (config.isPostLogger()) isPostLogger(response, id);
                       }));
            }

            isPreLogger(request, id);
            return chain.filter(exchange)
            		.then(Mono.fromRunnable(() -> { 
            			 if (config.isPostLogger()) isPostLogger(response, id);
                    }));
        };
    }

    @Data
    public static class Config {
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }

    private void isPreLogger(ServerHttpRequest request, String id) {
        String clientIp = String.valueOf(request.getRemoteAddress());
        String uri = request.getURI().toString();
        String method = String.valueOf(request.getMethod());
        String header = request.getHeaders().toString();

        log.info("[Request-{}] {} >> [{}] {}", id, clientIp, method, uri);
        log.info("[Request-{}] Headers: {}", id, header);

        MultiValueMap<String, HttpCookie> cookies = request.getCookies();
        HttpCookie httpCookie = cookies.getFirst("refreshToken");
        if (httpCookie != null) {
            log.info("[Request-{}] refreshToken: {}", id, httpCookie.getValue());
        }
    }

    private void isPostLogger(ServerHttpResponse response, String id) {
        String header = response.getHeaders().toString();
        String status = String.valueOf(response.getStatusCode());

        log.info("[Response-{}] {} {}", id, status, header);

        MultiValueMap<String, ResponseCookie> cookies = response.getCookies();
        ResponseCookie cookie = cookies.getFirst("refreshToken");
        if (cookie != null) {
            log.info("[Response-{}] refreshToken: {}", id, cookie.getValue());
        }
    }
}