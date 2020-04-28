package com.checc.gateway.service;

import com.checc.gateway.config.FeignConfig;
import com.checc.gateway.entities.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient(value = "TOCC-PAYMENT-SERVICE",configuration = FeignConfig.class,fallback = PaymentFeignFallbackService.class)
public interface PaymentFeignService {

    @GetMapping(value = "/payment/get/{id}")
    public CommonResult getPaymentById(@PathVariable("id") Long id);
}
