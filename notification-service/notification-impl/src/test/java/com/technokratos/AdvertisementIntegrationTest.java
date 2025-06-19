package com.technokratos;

import com.technokratos.dto.EventInfoDTO;
import com.technokratos.service.properties.GoogleSmtpProperties;
import com.technokratos.util.RabbitVariables;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@EnableRabbit
public class AdvertisementIntegrationTest {

    @Container
    static RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:4.0-management-alpine");

    @DynamicPropertySource
    static void rabbitProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitMQContainer::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitMQContainer::getAdminPassword);
    }

    @Autowired
    RabbitTemplate rabbitTemplate;

    @MockitoBean
    JavaMailSender javaMailSender;

    @MockitoBean
    GoogleSmtpProperties googleSmtpProperties;

    @Test
    void sentValidEventInfoDTOShouldBePostToTheEmail() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        MimeMessage mimeMessage = mock(MimeMessage.class);

        when(googleSmtpProperties.getFromEmail()).thenReturn("test@test.com");

        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(javaMailSender).send(any(MimeMessage.class));

        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        EventInfoDTO eventInfoDTO = new EventInfoDTO(
                "test@test.com",
                "test",
                LocalDateTime.now(),
                List.of("123", "456"),
                "http://localhost:8080/123"
        );

        rabbitTemplate.convertAndSend(RabbitVariables.NOTIFICATION_EXCHANGE_NAME, RabbitVariables.EMAIL_ADVERTISEMENT_ROUTING_KEY, eventInfoDTO);

        boolean called = latch.await(10, TimeUnit.SECONDS);

        assertTrue(called, "Метод send() не был вызван");

        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(mimeMessage);
    }

    @Test
    void sentNotValidEventInfoDTOShouldNotSendEmail() throws InterruptedException {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        EventInfoDTO invalidDto = new EventInfoDTO(
                "invalid-email",
                "test",
                LocalDateTime.now(),
                List.of("artist"),
                "http://localhost:8080/test"
        );
        rabbitTemplate.convertAndSend(
                RabbitVariables.NOTIFICATION_EXCHANGE_NAME,
                RabbitVariables.EMAIL_ADVERTISEMENT_ROUTING_KEY,
                invalidDto
        );
        Thread.sleep(1000);
        verify(javaMailSender, never()).send(any(MimeMessage.class));
    }

}
