package com.checc.gateway.service;

import com.checc.gateway.entities.CommonResult;
import org.springframework.stereotype.Component;

@Component
public class PaymentFeignFallbackService implements PaymentFeignService{
    @Override
    public CommonResult getPaymentById(Long id) {
        return new CommonResult(400,"Feign调用服务异常");
    }
}
