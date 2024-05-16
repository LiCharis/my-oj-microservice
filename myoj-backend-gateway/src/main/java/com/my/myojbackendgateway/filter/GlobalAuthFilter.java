package com.my.myojbackendgateway.filter;

import cn.hutool.core.text.AntPathMatcher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @author 黎海旭
 **/
@Component
public class GlobalAuthFilter implements GlobalFilter, Ordered {

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest serverHttpRequest= exchange.getRequest();
        String path = serverHttpRequest.getURI().getPath();
        //判断路径中是否包含inner，inner接口只允许服务器内部调用，外部不能调用
        if (antPathMatcher.match("/**/inner/**",path)){
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.FORBIDDEN);
            DataBufferFactory bufferFactory = response.bufferFactory();
            DataBuffer dataBuffer = bufferFactory.wrap("无权限".getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(dataBuffer));
        }
        // todo 这里可以通过JWT Token技术替代sesstion来做权限的校验，
        //  因为这里的session是从HttpRequest中取出来的，而这里是serverHttpRequest，对象类型不一致难以取出

        // todo 可以在网关实现Sentinel接口限流降级

        //否则放行
        return chain.filter(exchange);


    }


    /**
     * 实现ordered接口，设置该拦截器的处理优先级最高，以后如果有多个拦截器的的话，
     * 让处理访问内部接口的请求拦截器首先执行，不符合权限要求即可以省去其他拦截器的处理
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
