package com.algaworks.algaposts.ems_post_service.infrastructure.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_RESULT = "post-service.post-processing-result.v1.q";
    public static final String DEAD_LETTER_QUEUE_RESULT = "post-service.post-processing-result.v1.dlq";
    public static final String FANOUT_EXCHANGE_NAME_RESULT = "text-processor-service.text-calculated.v1.e";

    public static final String FANOUT_EXCHANGE_NAME_POST = "post-service.post-created.v1.e";

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public FanoutExchange exchange_post() {
        return ExchangeBuilder
                .fanoutExchange(FANOUT_EXCHANGE_NAME_POST)
                .build();
    }

    //@Bean
    public FanoutExchange exchange_result() {
        return ExchangeBuilder
                .fanoutExchange(FANOUT_EXCHANGE_NAME_RESULT)
                .build();
    }

    @Bean
    public Queue deadLetterQueueResult() {
        return QueueBuilder.durable(DEAD_LETTER_QUEUE_RESULT).build();
    }

    @Bean
    public Queue queue_result() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", "");
        args.put("x-dead-letter-routing-key", DEAD_LETTER_QUEUE_RESULT);

        return QueueBuilder.durable(QUEUE_RESULT).withArguments(args).build();
    }

    @Bean
    public Binding binding_result() {
        return BindingBuilder.bind(queue_result()).to(exchange_result());
    }

}
