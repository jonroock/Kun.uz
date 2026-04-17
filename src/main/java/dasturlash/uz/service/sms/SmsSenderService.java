package dasturlash.uz.service.sms;

import dasturlash.uz.dto.sms.SmsRequestDTO;
import dasturlash.uz.exceptions.AppBadException;
import dasturlash.uz.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SmsSenderService {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SmsTokenService smsTokenService;

    @Autowired
    private SmsHistoryService smsHistoryService;

    public void sendRegistrationSMS(String phone) {
        Integer smsCode = RandomUtil.fiveDigit();
//        String body = "<#>G'iybat.uz partali. Ro'yxatdan o'tish uchun tasdiqlash kodi (code) : " + smsCode; //
        String body = "Bu Eskiz dan test"; // test message
        try {
            sendSms(phone, body);
            smsHistoryService.save(phone, body, String.valueOf(smsCode));
        } catch (Exception e) {
            e.printStackTrace();
            throw new AppBadException("Something went wrong");
        }
    }

    private void sendSms(String phone, String body) {
        SmsRequestDTO smsRequestDTO = new SmsRequestDTO();
        smsRequestDTO.setMobile_phone(phone);
        smsRequestDTO.setMessage(body);

        String url = "https://notify.eskiz.uz/api/message/sms/send";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + smsTokenService.getToken());

        RequestEntity<SmsRequestDTO> request = RequestEntity
                .post(url)
                .headers(headers)
                .body(smsRequestDTO);

        restTemplate.exchange(request, String.class);
    }

}
