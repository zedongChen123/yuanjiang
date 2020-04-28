package com.checc.gateway.controller;

import com.checc.gateway.entities.CommonResult;
import com.checc.gateway.entities.Payment;
import com.checc.gateway.service.PaymentFeignService;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@RestController
@Slf4j
@DefaultProperties(defaultFallback = "order_Global_FallbackMethod")
public class OrderController {

    private static final String PAYMENT_URL  =  "http://CLOUD-PAYMENT-SERVICE";

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private PaymentFeignService paymentFeignService;

    @PostMapping(value = "/consumer/create")
    public CommonResult<Payment> create(Payment payment){
        return restTemplate.postForObject(PAYMENT_URL+"/payment/create",payment,CommonResult.class);
    }

    @GetMapping(value = "/consumer/get/{id}")
    public CommonResult<Payment> getPaymentById(@PathVariable("id") Long id){
        return restTemplate.getForObject(PAYMENT_URL+"/payment/get/"+id,CommonResult.class);
    }

    @GetMapping(value = "/consumer/getFeign/{id}")
//    @HystrixCommand
//    @HystrixCommand(fallbackMethod = "getPaymentFeignByIdExceptionHandler",
//            commandProperties = {@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "1500")})
    public CommonResult<Payment> getPaymentFeignById(@PathVariable("id") Long id){
        return paymentFeignService.getPaymentById(id);
    }

    //异常出现,客户端指定降级方法
    public CommonResult<Payment> getPaymentFeignByIdExceptionHandler(@PathVariable("id") Long id){
        log.info("服务方法降级");
        return new CommonResult(400,"调用服务异常");
    }

    // 全局异常降级处理方法
    public CommonResult<Payment> order_Global_FallbackMethod(){
        log.info("全局服务方法降级");
        return new CommonResult(400,"通用调用服务异常");
    }


}
