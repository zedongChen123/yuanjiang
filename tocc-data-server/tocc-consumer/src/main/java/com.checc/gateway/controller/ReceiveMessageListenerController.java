package com.checc.gateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@EnableBinding(Sink.class)
@Slf4j
public class ReceiveMessageListenerController {

    @StreamListener(Sink.INPUT)
    public void input(Message<String> message){
        log.info("******接受消息："+message.getPayload()+"\t");
    }
}
