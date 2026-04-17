package dasturlash.uz.controller;

import dasturlash.uz.dto.EmailHistoryDTO;
import dasturlash.uz.service.email.EmailHistoryService;
import dasturlash.uz.util.PageUtil;
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
@RequestMapping("/api/v1/email-history")
public class EmailHistoryController {
    @Autowired
    private EmailHistoryService emailHistoryService;

    @GetMapping("/by-email")
    public ResponseEntity<List<EmailHistoryDTO>> getEmailHistoryByEmail(@RequestParam("email") String email) {
        List<EmailHistoryDTO> history = emailHistoryService.getEmailHistoryByEmail(email);
        return ResponseEntity.ok(history); // Return 200 OK with the list of DTOs
    }

    @GetMapping("/by-date")
    public ResponseEntity<List<EmailHistoryDTO>> getEmailHistoryByDate(@RequestParam("date") LocalDate date) {
        List<EmailHistoryDTO> history = emailHistoryService.getEmailHistoryByDate(date);
        if (history.isEmpty()) {
            return ResponseEntity.noContent().build(); // Return 204 No Content if no history found
        }
        return ResponseEntity.ok(history); // Return 200 OK with the list of DTOs
    }

    @GetMapping("/pagination")
    public ResponseEntity<Page<EmailHistoryDTO>> getPaginatedEmailHistory(@RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "10") int size) {
        Page<EmailHistoryDTO> paginatedHistory = emailHistoryService.getPaginatedEmailHistory(PageUtil.page(page), size);
        return ResponseEntity.ok(paginatedHistory); // Return 200 OK with the paginated DTOs
    }

}
