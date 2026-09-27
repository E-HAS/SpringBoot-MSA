package com.msa.gateway.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;

import com.msa.gateway.jwt.base.JwtTokenValidBase;

import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {

    private static final String REQUEST_ID_HEADER = "X-Request-Id";

    // 로그에 원문을 남기면 안 되는 민감 헤더/쿠키 목록
    private static final List<String> SENSITIVE_HEADERS = List.of("authorization", "cookie", "set-cookie");

    public GlobalFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String id = request.getId();

            // 다운스트림 서비스(authservice 등)까지 동일한 requestId를 전파
            // -> 게이트웨이~서비스 로그를 하나의 id로 grep해서 추적 가능
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header(REQUEST_ID_HEADER, id)
                    .build();
            ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();
            ServerHttpResponse response = mutatedExchange.getResponse();

            log.info("[Filter-{}] baseMessage : {}", id, config.getBaseMessage());

            if (config.isPreLogger()) {
                logRequest(mutatedRequest, id);
            }

            long startTime = System.currentTimeMillis();

            return chain.filter(mutatedExchange)
                    .doOnError(e -> log.error("[Request-{}] Filter chain error: {}", id, e.getMessage(), e))
                    .then(Mono.fromRunnable(() -> {
                        if (config.isPostLogger()) {
                            long duration = System.currentTimeMillis() - startTime;
                            logResponse(response, id, duration);
                        }
                    }));
        };
    }

    @Data
    public static class Config {
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }

    private void logRequest(ServerHttpRequest request, String id) {
        String clientIp = String.valueOf(request.getRemoteAddress());
        String uri = request.getURI().toString();
        String method = String.valueOf(request.getMethod());

        log.info("[Request-{}] {} >> [{}] {}", id, clientIp, method, uri);

        logHeaders(id, request.getHeaders(), request.getCookies());
    }

    private void logResponse(ServerHttpResponse response, String id, long durationMs) {
        String status = String.valueOf(response.getStatusCode());

        boolean isError = status.startsWith("4") || status.startsWith("5");
        if (isError) {
            log.error("[Response-{}] {} ({}ms)", id, status, durationMs);
        } else {
            log.info("[Response-{}] {} ({}ms)", id, status, durationMs);
        }

        logHeaders(id, response.getHeaders(), response.getCookies());
    }

    private void logHeaders(String id, HttpHeaders headers, MultiValueMap<String, ? extends HttpCookie> cookies) {
        if (log.isDebugEnabled()) {
            // Authorization/Cookie 헤더 추출
            log.debug("[Response-{}] Headers: {}", id, getHeaders(headers));
        }

        // refreshtoken 추출
        HttpCookie cookie = cookies.getFirst(JwtTokenValidBase.REFRESH_COOKIE_NAME);
        if (cookie != null) {
            log.info("[Response-{}] refreshToken: {}", id, cookie.getValue());
        }
    }

    // Authorization/Cookie 헤더 추출
    private String getHeaders(HttpHeaders headers) {
        StringBuilder sb = new StringBuilder("{");
        headers.forEach((key, values) -> {
            boolean find = SENSITIVE_HEADERS.contains(key.toLowerCase());
            if(find){
                sb.append(key).append("=");
                sb.append(values);
                sb.append(", ");
            }
        });
        sb.append("}");
        return sb.toString();
    }
}