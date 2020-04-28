package com.checc.gateway.Handler;

import com.checc.common.constant.Constants;
import com.checc.common.redis.util.RedisUtils;
import com.checc.common.utils.CaptchaUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@AllArgsConstructor
public class ImageCodeHandler implements HandlerFunction<ServerResponse>
{
    @Autowired
    private RedisUtils redisCache;

    @Override
    public Mono<ServerResponse> handle(ServerRequest serverRequest)
    {

        // 生成图片验证码
        BufferedImage image = CaptchaUtil.createImage();
        // 生成文字验证码
        String randomText = CaptchaUtil.drawRandomText(image);
        // 唯一标识
        String uuid = serverRequest.queryParam("uuid").get();
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;

        redisCache.set(verifyKey, randomText.toLowerCase(), Constants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpeg", os);
        }
        catch (IOException e)
        {
            log.error("验证码生成失败", e);
            return Mono.error(e);
        }

        return ServerResponse
                .status(HttpStatus.OK)
                .contentType(MediaType.IMAGE_JPEG)
                .body(BodyInserters.fromResource(new ByteArrayResource(os.toByteArray())));
    }
}