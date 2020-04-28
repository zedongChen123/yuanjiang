package  com.checc.gateway.filter;

import java.net.URI;

import cn.hutool.core.util.StrUtil;
import com.checc.common.constant.Constants;
import com.checc.common.core.domain.R;
import com.checc.common.exception.ValidateCodeException;
import com.checc.common.redis.util.RedisUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import lombok.SneakyThrows;
import reactor.core.publisher.Mono;


/**
 * 验证码处理
 */
@Component
public class ImageCodeFilter extends AbstractGatewayFilterFactory<ImageCodeFilter.Config>
{
    private final static String AUTH_URL = "/auth/login";

    @Autowired
    private RedisUtils redisCache;

    public ImageCodeFilter()
    {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config)
    {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            URI uri = request.getURI();
            // 不是登录请求，直接向下执行
            if (!StringUtils.containsIgnoreCase(uri.getPath(), AUTH_URL))
            {
                return chain.filter(exchange);
            }
            try
            {
                // 检验验证码code
                validateCode(request);
            }
            catch (Exception e)
            {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
                String msg = JSON.toJSONString(R.error(e.getMessage()));
                DataBuffer bodyDataBuffer = response.bufferFactory().wrap(msg.getBytes());
                return response.writeWith(Mono.just(bodyDataBuffer));
            }
            return chain.filter(exchange);
        };
    }


    /**
     * 验证流程
     *
     * @param request
     */
    @SneakyThrows
    private void validateCode(ServerHttpRequest  request) {

        String captcha = obtainImageCode(request);
        String uuid = obtainUid(request);
        // 验证验证码
        if (StrUtil.isBlank(captcha)) {
            throw new ValidateCodeException("验证码不能为空");
        }
        if (StringUtils.isBlank(uuid))
        {
            throw new ValidateCodeException("验证码不合法");
        }
        if (!captcha.equals("1111")) {  //测试验证码 1111 临时添加
            // 从redis中获取之前保存的验证码跟前台传来的验证码进行匹配
            Object kaptcha = redisCache.get(Constants.CAPTCHA_CODE_KEY + uuid);
            if (kaptcha == null) {
                throw new ValidateCodeException("验证码已失效");
            }
            if (!captcha.toLowerCase().equals(kaptcha)) {
                throw new ValidateCodeException("验证码错误");
            }
        }

    }

    private String obtainUid(ServerHttpRequest request) {
        String imageId = "uuid";
        return request.getQueryParams().getFirst(imageId);
    }

    /**
     * 获取前端传来的图片验证码
     *
     * @param request
     * @return
     */
    private String obtainImageCode(ServerHttpRequest request) {
        String imageCode = "code";
        return request.getQueryParams().getFirst(imageCode);
    }


    public static class Config
    {
    }
}
