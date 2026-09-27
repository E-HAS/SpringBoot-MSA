package com.msa.gateway.jwt.filter;

import com.msa.gateway.jwt.service.JwtTokenValidService;
import io.jsonwebtoken.Claims;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private final JwtTokenValidService jwtTokenValidService;

    public JwtAuthenticationFilter(JwtTokenValidService jwtTokenValidService) {
        super(Config.class);
        this.jwtTokenValidService = jwtTokenValidService;
    }

    @Data
    public static class Config {
        private List<String> excludePaths;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            // 예외 경로 처리
            if (isExcludedPath(request, config.getExcludePaths())) {
                return chain.filter(exchange);
            }

            return jwtTokenValidService.resolveAccessToken(request)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No Authorization header or Invalid format")))
                    .flatMap(jwt ->
                            jwtTokenValidService.validateAccessToken(jwt) // AccessToken 블랙리스트 조회
                                    .then(jwtTokenValidService.validateTokenAndGetClaims(jwt)) // 토큰 추출
                                    .onErrorMap(e -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid or Expired Access token: " + e.getMessage(),e))
                    )
                    .map(Claims::getSubject)
                    // 헤더 추가
                    .flatMap(userId -> {
                        ServerHttpRequest modifiedRequest = request.mutate()
                                .header("X-User-Id", userId)
                                .build();
                        // 라우팅 전송
                        return chain.filter(exchange.mutate().request(modifiedRequest).build());
                    })
                    // AccessToken, RefreshToken, Redis 조회 등 모든 에러 401 반환
                    .onErrorResume(e -> onError(exchange, e.getMessage(), HttpStatus.UNAUTHORIZED));
        };
    }
    // 에외 경로 처리
    private boolean isExcludedPath(ServerHttpRequest request, List<String> excludePaths) {
        if (excludePaths == null || excludePaths.isEmpty()) {
            return false;
        }

        String requestKey = request.getMethod().name() + ":" + request.getURI().getPath();

        return excludePaths.stream()
                .anyMatch(pattern -> requestKey.matches(pattern.replace("*", ".*")));
    }
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();

        response.setStatusCode(httpStatus);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = "{\"message\":\"" + err + "\"}";
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }
}