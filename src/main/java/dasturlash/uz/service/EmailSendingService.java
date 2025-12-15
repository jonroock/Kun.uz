package dasturlash.uz.service;

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
public class EmailSendingService {
    @Value("${spring.mail.username}")
    private String fromAccount;
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private EmailHistoryService emailHistoryService;

    public void sendRegistrationEmail(String toAccount) {
        Integer smsCode = RandomUtil.fiveDigit();
        String body = "Your confirmation code: " + smsCode;
        // send
        sendSimpleMessage("Registration complete", body, toAccount);
    }


    public String sendSimpleMessage(String subject, String content, String toAccount) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(fromAccount);
        msg.setTo(toAccount);
        msg.setSubject(subject);
        msg.setText(content);
        javaMailSender.send(msg);

        return "Mail was send";
    }

    public void sendRegistrationStyledEmail(String toAccount) {
        Integer smsCode = RandomUtil.fiveDigit();
        String body = """
                 <!DOCTYPE html>
                <html lang="en">
                <head>
                <meta charset="UTF-8" />
                <style>
                body {
            font-family: Arial, sans-serif;
            background: #f2f2f7;
            padding: 0;
            margin: 0;
        }
        .container {
            max-width: 480px;
            background: #ffffff;
            margin: 40px auto;
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 8px 25px rgba(0,0,0,0.08);
        }
        .title {
            font-size: 26px;
            font-weight: bold;
            text-align: center;
            color: #333;
            margin-bottom: 20px;
        }
        .description {
            font-size: 16px;
            text-align: center;
            color: #666;
            margin-bottom: 25px;
        }
        .code-box {
            font-size: 36px;
            font-weight: bold;
            text-align: center;
            background: linear-gradient(135deg, #4f46e5, #6d28d9);
            color: white;
            letter-spacing: 6px;
            padding: 18px 0;
            border-radius: 10px;
            margin-bottom: 30px;
        }
        .footer {
            text-align: center;
            font-size: 13px;
            color: #999;
            margin-top: 15px;
        }
    </style>
                </head>
                <body>
                <div class="container">
                <div class="title">Verify Your Email</div>

                <div class="description">
                To complete your registration, please enter the verification code below:
    </div>

                <div class="code-box">%d</div>

                <div class="description">
                This code is valid for <b>2 minutes</b>.  
    </div>

                <div class="footer">
        © 2025 Captain John. All rights reserved.
                </div>
                </div>
                </body>
                </html>""";
        body = String.format(body, smsCode);
        // send
        sendMimeMessage("Registration complete", body, toAccount);
        // save to db
        emailHistoryService.create(body, smsCode, toAccount);
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
