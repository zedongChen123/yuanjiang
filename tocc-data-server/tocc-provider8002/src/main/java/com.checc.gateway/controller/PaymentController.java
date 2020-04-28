package com.checc.gateway.controller;

import com.checc.gateway.entities.CommonResult;
import com.checc.gateway.entities.Payment;
import com.checc.gateway.service.PaymentService;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@Slf4j
public class PaymentController {

    @Resource
    private PaymentService paymentService;

    @Value("${server.port}")
    private String serverPort;

    @PostMapping(value = "/payment/create")
    public CommonResult<Payment> create(@RequestBody Payment payment){
        int result = paymentService.create(payment);
        log.info("***插入结果："+result);
        if (result >0){
            return new CommonResult(200,"成功",result);
        }else{
            return new  CommonResult(400,"失败");
        }
    }

    @GetMapping(value = "/payment/get/{id}")
    public CommonResult getPaymentById(@PathVariable("id") Long id){
        Payment payment = paymentService.getPaymentById(id);
        log.info("***查询结果："+ payment);
        if (payment != null){
            return new CommonResult(200,serverPort+"成功",payment);
        }else{
            return new  CommonResult(400,"失败");
        }
    }
}
