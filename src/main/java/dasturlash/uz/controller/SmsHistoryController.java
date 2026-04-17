package dasturlash.uz.controller;

import dasturlash.uz.dto.sms.SmsHistoryDTO;
import dasturlash.uz.service.sms.SmsHistoryService;
import dasturlash.uz.util.PageUtil;
import dasturlash.uz.util.PhoneUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sms-history")
public class SmsHistoryController {
    @Autowired
    private SmsHistoryService smsHistoryService;

    @GetMapping("/by-phone")
    public ResponseEntity<List<SmsHistoryDTO>> getSmsHistoryByEmail(@RequestParam("phone") String phone) {
        List<SmsHistoryDTO> history = smsHistoryService.getSmsHistoryByPhone(PhoneUtil.toLocalPhone(phone));
        return ResponseEntity.ok(history);
    }

    @GetMapping("/by-date")
    public ResponseEntity<List<SmsHistoryDTO>> getEmailHistoryByDate(@RequestParam("date") LocalDate date) {
        List<SmsHistoryDTO> history = smsHistoryService.getSmsHistoryByDate(date);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/pagination")
    public ResponseEntity<Page<SmsHistoryDTO>> getPaginatedPhoneHistory(@RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size) {
        Page<SmsHistoryDTO> paginatedHistory = smsHistoryService.getPaginatedSmsHistory(PageUtil.page(page), size);
        return ResponseEntity.ok(paginatedHistory);
    }

}
