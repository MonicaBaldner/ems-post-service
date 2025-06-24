package com.algaworks.algaposts.ems_post_service.infrastructure.rabbitmq;


import com.algaworks.algaposts.ems_post_service.api.model.PostResult;
import com.algaworks.algaposts.ems_post_service.domain.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;

import static com.algaworks.algaposts.ems_post_service.infrastructure.rabbitmq.RabbitMQConfig.QUEUE_RESULT;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMQListener {

    private final PostService postService;

    @SneakyThrows
    @RabbitListener(queues = QUEUE_RESULT)
    public void handle(@Payload PostResult postResult){

        log.info("Está no listener de post-service com o resultado monetizado");
        log.info("Recebeu resultado {} na fila {} ", postResult, QUEUE_RESULT);

        String id = postResult.getPostId();
        Integer wordCount = postResult.getWordCount();
        BigDecimal calculatedValue = postResult.getCalculatedValue();
        log.info("Message Received: Id {} WordCount {} CalculatedValue {}", id, wordCount, calculatedValue);

        //Thread.sleep(Duration.ofSeconds(15));

        postService.upddatePost(postResult);
    }

}
