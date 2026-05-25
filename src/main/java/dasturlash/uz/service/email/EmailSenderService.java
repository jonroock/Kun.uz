package dasturlash.uz.service.email;

import dasturlash.uz.service.email.EmailHistoryService;
import dasturlash.uz.util.JwtUtil;
import dasturlash.uz.util.RandomUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {
    @Value("${spring.mail.username}")
    private String fromAccount;
    @Value("${server.url}")
    private String serverUrl;
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private EmailHistoryService emailHistoryService;

    public void sendRegistrationEmail(String toAccount) {
        Integer smsCode = RandomUtil.fiveDigit();
        String body = "Click there: %s/api/v1/auth/registration/email/verification/%s";
        String jwtToken = JwtUtil.encodeForRegistration(toAccount, smsCode);
        body = String.format(body, serverUrl, jwtToken);
        // send
        sendSimpleMessage("Registration complete", body, toAccount);
        // save to db
        emailHistoryService.create(body, smsCode, toAccount);
    }

    public void sendRegistrationStyledEmail(String toAccount) {
        Integer smsCode = RandomUtil.fiveDigit();
        String body = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Kun.uz Verification</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h1 style=\"text-align: center\">Kun.uz Portaliga xush kelibsiz!</h1>\n" +
                "<h4>Ro'yhatdan o'tishni tugatish uchun quyidagi kodni kiriting:</h4>\n" +
                "<h2 style=\"text-align: center; background-color: #e63946; color: white; " +
                "padding: 20px; letter-spacing: 10px; font-size: 36px;\">%d</h2>\n" +
                "<p>Bu kod 60 daqiqa davomida amal qiladi.</p>\n" +
                "</body>\n" +
                "</html>";
        body = String.format(body, smsCode);
        sendMimeMessage("Kun.uz - Verification Code", body, toAccount);
        emailHistoryService.create(body, smsCode, toAccount);
    }

    private String sendSimpleMessage(String subject, String body, String toAccount) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(fromAccount);
        msg.setTo(toAccount);
        msg.setSubject(subject);
        msg.setText(body);
        javaMailSender.send(msg);

        return "Mail was send";
    }

    private String sendMimeMessage(String subject, String body, String toAccount) {
        try {
            MimeMessage msg = javaMailSender.createMimeMessage();
            msg.setFrom(fromAccount);

            MimeMessageHelper helper = new MimeMessageHelper(msg, true);
            helper.setTo(toAccount);
            helper.setSubject(subject);
            helper.setText(body, true);
            javaMailSender.send(msg);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return "Mail was send";
    }
}
