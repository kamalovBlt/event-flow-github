package com.technokratos.config;

import com.technokratos.util.RabbitVariables;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {


    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jackson2JsonMessageConverter());
        return template;
    }


    @Bean
    public DirectExchange notificationsDirectExchange() {
        return new DirectExchange(RabbitVariables.NOTIFICATION_EXCHANGE_NAME);
    }

    @Bean
    public Queue emailAdvertisementQueue() {
        return new Queue(RabbitVariables.EMAIL_ADVERTISEMENT_QUEUE_NAME);
    }

    @Bean
    public Binding emailAdvertisementsBinding(
            Queue emailAdvertisementQueue, DirectExchange notificationsDirectExchange
    ) {
        return BindingBuilder
                .bind(emailAdvertisementQueue)
                .to(notificationsDirectExchange)
                .with(RabbitVariables.EMAIL_ADVERTISEMENT_ROUTING_KEY);
    }

    @Bean
    public Queue emailCodeQueue() {
        return new Queue(RabbitVariables.EMAIL_CODE_QUEUE_NAME);
    }

    @Bean
    public Binding emailCodeBinding(
            Queue emailCodeQueue, DirectExchange notificationsDirectExchange
    ) {
        return BindingBuilder
                .bind(emailCodeQueue)
                .to(notificationsDirectExchange)
                .with(RabbitVariables.EMAIL_CODE_ROUTING_KEY);
    }

}
