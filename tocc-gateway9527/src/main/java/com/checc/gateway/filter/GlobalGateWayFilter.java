package com.checc.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.checc.common.constant.Constants;
import com.checc.common.core.domain.R;
import com.checc.common.utils.StringUtils;
import com.checc.gateway.service.TokenService;
import com.checc.system.domain.SysLoginUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;

/**
 * 全局自定义拦截过滤配置
 */
@Component
@Slf4j
public class GlobalGateWayFilter implements GlobalFilter, Ordered {
    // 排除过滤的 uri 地址
    // swagger排除自行添加
    private static final String[]           whiteList = {"/login", "/user/register", "/system/v2/api-docs"};

    @Autowired
    private TokenService tokenService;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("***********进入全局过滤器********"+new Date());
        String url = exchange.getRequest().getURI().getPath();
        log.info("url:{}", url);
        // 跳过不需要验证的路径
        if (Arrays.asList(whiteList).contains(url))
        {
            return chain.filter(exchange);
        }
        String token = exchange.getRequest().getHeaders().getFirst(Constants.TOKEN);
        // token为空
        if (StringUtils.isBlank(token))
        {
            return setUnauthorizedResponse(exchange, "token 不能为空");
        }
        SysLoginUser loginUser = tokenService.getUser(token);
        if (StringUtils.isNull(loginUser)){
            log.info("*****用户名或密码错误****");
            exchange.getResponse().setStatusCode(HttpStatus.NOT_ACCEPTABLE);
            return setUnauthorizedResponse(exchange,"用户名或密码错误");
        }
        ServerHttpRequest mutableReq = exchange.getRequest().mutate()
                .header(Constants.TOKEN,token)
                .header(Constants.CURRENT_USERNAME, loginUser.getUser().getUserName()).build();
        ServerWebExchange mutableExchange = exchange.mutate().request(mutableReq).build();
        return chain.filter(mutableExchange);
    }

    private Mono<Void> setUnauthorizedResponse(ServerWebExchange exchange, String msg)
    {
        ServerHttpResponse originalResponse = exchange.getResponse();
        originalResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
        originalResponse.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        byte[] response = null;
        try
        {
            response = JSON.toJSONString(R.error(401, msg)).getBytes(Constants.UTF8);
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        DataBuffer buffer = originalResponse.bufferFactory().wrap(response);
        return originalResponse.writeWith(Flux.just(buffer));
    }

    @Override
    public int getOrder() {
        return -200;
    }
}
