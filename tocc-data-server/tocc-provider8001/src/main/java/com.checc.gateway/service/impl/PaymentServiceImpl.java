package com.checc.gateway.service.impl;

import com.checc.gateway.dao.PaymentDao;
import com.checc.gateway.entities.Payment;
import com.checc.gateway.service.PaymentService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    @Resource
    private PaymentDao paymentDao;

    @Override
    public int create(Payment payment) {
        return paymentDao.create(payment);
    }

    // 服务熔断
    @HystrixCommand(fallbackMethod = "getPaymentByIdExceptionHandler", commandProperties = {
            @HystrixProperty(name = "circuitBreaker.enabled",value = "true"),// 是否开启断路器
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold",value = "10"),// 请求次数
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds",value = "10000"),// 时间窗口期
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage",value = "60")// 失败率达到多少后断路
    })
    @Override
    public Payment getPaymentById(Long id) {
        if (id<0){
            throw  new RuntimeException("....id 不能为负数");
        }
        try {
            return paymentDao.getPaymentById(id);
        } catch (Exception e) {
            return null;
        }
    }

    //异常出现降级方法
    public Payment getPaymentByIdExceptionHandler(Long id){
        log.info("服务方法降级");
        return null;
    }
}
