package com.technokratos.service.impl;

import com.technokratos.model.EventInfo;
import com.technokratos.service.api.AdvertisementService;
import com.technokratos.service.properties.GoogleSmtpProperties;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailAdvertisementService implements AdvertisementService {

    private static final String TEMPLATE_NAME = "email-advertisement.ftl";
    private static final String EMAIL_SUBJECT_MESSAGE = "EVENT FLOW - НОВОЕ МЕРОПРИЯТИЕ";

    private final JavaMailSender javaMailSender;
    private final Configuration freemarkerConfig;
    private final GoogleSmtpProperties googleSmtpProperties;

    @Override
    public void sendAdvertisement(EventInfo eventInfo, String email) {
        try {
            Template template = freemarkerConfig.getTemplate(TEMPLATE_NAME);
            Map<String, Object> model = new HashMap<>();
            model.put("event", eventInfo);
            StringWriter stringWriter = new StringWriter();
            template.process(model, stringWriter);
            String htmlContent = stringWriter.toString();
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessageHelper.setFrom(googleSmtpProperties.getFromEmail());
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject(EMAIL_SUBJECT_MESSAGE);
            mimeMessageHelper.setText(htmlContent, true);
            javaMailSender.send(mimeMessage);
        } catch (IOException | TemplateException e) {
            log.info("Не удалось загрузить сообщение {} {}", eventInfo, e.getMessage());
        } catch (MessagingException e) {
            log.info("Не удалось отправить уведомление на почту {} с сообщением {}", email, eventInfo.toString());
        }
    }

}
