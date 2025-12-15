package dasturlash.uz.service;

import dasturlash.uz.dto.sms.SmsProviderTokenDTO;
import dasturlash.uz.dto.sms.SmsRequestDTO;
import dasturlash.uz.dto.sms.SmsTokenProviderResponse;
import dasturlash.uz.exceptions.AppBadException;
import dasturlash.uz.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
@Service
public class SmsSenderService {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${sms.eskiz.email}")
    private String email;
    @Value("${sms.eskiz.password}")
    private String password;
    private final String url = "https://notify.eskiz.uz/api/";

    private String getToken() {

        SmsProviderTokenDTO smsProviderTokenDTO = new SmsProviderTokenDTO();
        smsProviderTokenDTO.setEmail(email);
        smsProviderTokenDTO.setPassword(password);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        RequestEntity<SmsProviderTokenDTO> request = RequestEntity
                .post(url + "auth/login")
                .headers(headers)
                .body(smsProviderTokenDTO);

        var response = restTemplate.exchange(request, SmsTokenProviderResponse.class);
        String token = response.getBody().getData().getToken();
        return token;
    }

    private void sendSms(String phone, String body) {
        SmsRequestDTO smsRequestDTO = new SmsRequestDTO();
        smsRequestDTO.setMobile_phone(phone);
        smsRequestDTO.setMessage(body);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + getToken());

        RequestEntity<SmsRequestDTO> request = RequestEntity
                .post(url +"message/sms/send")
                .headers(headers)
                .body(smsRequestDTO);

        restTemplate.exchange(request, String.class);
    }

    public void sendRegistrationSMS(String phone) {
        Integer smsCode = RandomUtil.fiveDigit();
        String body = "<#>bormi.uz chegirmalar do'konidan ro'yxatdan o'tish uchun tasdiqlash kodi: " + smsCode; // test message
        try {
            sendSms(phone, body);
        } catch (Exception e) {
            e.printStackTrace();
            throw new AppBadException("Something went wrong");
        }
    }




}
