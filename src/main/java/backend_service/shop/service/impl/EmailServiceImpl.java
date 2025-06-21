package backend_service.shop.service.impl;

import backend_service.shop.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.io.File;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    /**
     * @param to
     * @param subject
     * @param content
     * @param pdfAttachment
     */
    @Override
    public void sendInvoiceEmail(String to, String subject, String content, File pdfAttachment) {
        log.info("Send mail loading.................");

        try {

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, false);
            helper.addAttachment(pdfAttachment.getName(), pdfAttachment);

            mailSender.send(message);

            log.info("Send mail successfully {}", to);

        } catch (MessagingException e) {
            log.info("Send mail fail {}", e.getMessage());
            throw new RuntimeException("Lỗi gửi email", e);
        }
    }
}
