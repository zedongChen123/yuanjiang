package com.checc.auth.controller;

import com.checc.common.constant.Constants;
import com.checc.common.redis.util.RedisUtils;
import com.checc.common.utils.CaptchaUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 验证码操作处理
 *
 * @author checc
 */
@Slf4j
@RestController
public class SysCaptchaController {

    @Autowired
    private RedisUtils redisCache;

    @GetMapping("/captchaImage")
    public void captcha(HttpServletResponse response, HttpServletRequest request) throws IOException {
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");
        // 生成图片验证码
        BufferedImage image = CaptchaUtil.createImage();
        // 生成文字验证码
        String randomText = CaptchaUtil.drawRandomText(image);
        // 唯一标识
        String uuid = request.getParameter("uuid");
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;

        redisCache.set(verifyKey, randomText.toLowerCase(), Constants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpeg", os);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("ImageIO write err", e);
        }
    }
}
