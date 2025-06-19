package com.technokratos.service.impl;

import com.technokratos.dto.VerifyCodeDTO;
import com.technokratos.service.api.VerifyCodeSender;
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
public class EmailVerifyCodeSender implements VerifyCodeSender {

    private static final String TEMPLATE_NAME = "email-code.ftl";
    private static final String EMAIL_SUBJECT_MESSAGE = "EVENT FLOW - Код подтверждения";

    private final JavaMailSender javaMailSender;
    private final Configuration freemarkerConfig;
    private final GoogleSmtpProperties googleSmtpProperties;

    @Override
    public void sendVerifyCode(VerifyCodeDTO verifyCodeDTO) {
        try {
            Template template = freemarkerConfig.getTemplate(TEMPLATE_NAME);
            Map<String, Object> model = new HashMap<>();
            model.put("code", verifyCodeDTO.code());
            StringWriter stringWriter = new StringWriter();
            template.process(model, stringWriter);
            String htmlContent = stringWriter.toString();
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessageHelper.setFrom(googleSmtpProperties.getFromEmail());
            mimeMessageHelper.setTo(verifyCodeDTO.email());
            mimeMessageHelper.setSubject(EMAIL_SUBJECT_MESSAGE);
            mimeMessageHelper.setText(htmlContent, true);
            javaMailSender.send(mimeMessage);
        } catch (IOException | TemplateException e) {
            log.info("Не удалось загрузить сообщение {} {}", verifyCodeDTO, e.getMessage());
        } catch (MessagingException e) {
            log.info("Не удалось отправить уведомление на почту {} с сообщением {}", verifyCodeDTO.email(), verifyCodeDTO.code());
        }
    }
}
