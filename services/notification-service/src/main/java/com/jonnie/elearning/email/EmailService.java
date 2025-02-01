package com.jonnie.elearning.email;


import com.jonnie.elearning.kafka.cart.CartItemNotifyResponse;
import com.jonnie.elearning.utils.EmailTemplates;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_RELATED;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    // method to prepare and send email
    private void prepareAndSendEmail(String destinationEmail, EmailTemplates emailTemplates, Map<String, Object> variables) {
        try {
            if (destinationEmail == null || destinationEmail.trim().isEmpty()) {
                log.error("Failed to send email: Email address is null or empty.");
                return;
            }

            // ✅ Validate email using Java Mail API
            InternetAddress emailAddr = new InternetAddress(destinationEmail);
            emailAddr.validate();

            // Create email
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(
                    mimeMessage,
                    MULTIPART_MODE_RELATED,
                    StandardCharsets.UTF_8.name()
            );
            messageHelper.setFrom("jonnie@proton.me");
            messageHelper.setSubject(emailTemplates.getSubject());
            messageHelper.setTo(destinationEmail);

            Context context = new Context();
            context.setVariables(variables);

            String html = templateEngine.process(emailTemplates.getTemplateName(), context);
            messageHelper.setText(html, true);
            javaMailSender.send(mimeMessage);
            log.info("Email sent successfully to: {}", destinationEmail);

        } catch (AddressException e) {
            log.error(" Invalid recipient email address: {}", destinationEmail);
        } catch (MessagingException e) {
            log.error("Error sending email to <{}>", destinationEmail);
        }
    }

    @Async
    public void sendCartConfirmationEmail(
            String destinationEmail,
            String customerName,
            BigDecimal totalAmount,
            String cartReference,
            List<CartItemNotifyResponse> cartItems
    ) {
        if (destinationEmail != null) {
            destinationEmail = destinationEmail.trim().toLowerCase();
        }

        // Log the structure of cartItems
        log.info("Cart Items: {}", cartItems);

        Map<String, Object> variables = Map.of(
                "customerName", customerName,
                "totalAmount", totalAmount,
                "cartReference", cartReference,
                "cartItems", cartItems
        );

        // Pass the variables to the template
        prepareAndSendEmail(destinationEmail, EmailTemplates.CART_CONFIRMATION, variables);
    }

    //method to send payment confirmation email
    @Async
    public void sendPaymentConfirmationEmail(String destinationEmail,
                                             String customerName,
                                             BigDecimal amount,
                                             String cartReference) {
        if (destinationEmail != null) {
            destinationEmail = destinationEmail.trim().toLowerCase();
        }

        Map<String, Object> variables = Map.of(
                "customerName", customerName,
                "amount", amount,
                "cartReference", cartReference
        );
        prepareAndSendEmail(destinationEmail, EmailTemplates.PAYMENT_CONFIRMATION, variables);
    }

}
