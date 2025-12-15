package dasturlash.uz.service;

import dasturlash.uz.dto.EmailHistoryDTO;
import dasturlash.uz.dto.sms.SmsHistoryDTO;
import dasturlash.uz.entity.EmailHistoryEntity;
import dasturlash.uz.entity.SmsHistoryEntity;
import dasturlash.uz.repository.SmsHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SmsHistoryService {

    @Autowired
    private SmsHistoryRepository repository;

    public void create(String body, int code, String toAccount) {
        SmsHistoryEntity entity = new SmsHistoryEntity();
        entity.setBody(body);
        entity.setCode(code);
        entity.setToAccount(toAccount);
        entity.setCreatedAt(LocalDateTime.now());
        repository.save(entity);
    }

    public boolean isSmsSendToAccount(String account, int code) {
        repository.findFirstByPhoneOrderByCreatedAtDesc(account)
                .map(entity -> {
                    if (!entity.getCode().equals(code)) {
                        System.out.println("Kodni xato kiritdingiz");
                        return false;
                    }
                    LocalDateTime expDate = entity.getCreatedAt().plusMinutes(5);
                    if (LocalDateTime.now().isAfter(expDate)) {
                        System.out.println("Kodni vaqti o'tib ketdi!");
                        return false;
                    }
                    return true;
                })
                .orElseGet(() -> {
                    System.out.println("Sms yetib kelmadi!");
                    return false;
                });
        return false;
    }

    public List<SmsHistoryDTO> getSmsHistoryByPhone(String phone) {
        // Find entities by toAccount (which is 'phone' in the DTO context)
        List<SmsHistoryEntity> entities = repository.findByPhoneOrderByCreatedAtDesc(phone);
        // Convert entities to DTOs and return
        return entities.stream().map(entity -> toDto(entity)).collect(Collectors.toList());
    }

    public List<SmsHistoryDTO> getSmsHistoryByDate(String phone, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();

        // Find entities created within the specified date range
        List<SmsHistoryEntity> entities = repository.findByPhoneAndCreatedAtAfterOrderByCreatedAtDesc(phone, startOfDay);
        // Convert entities to DTOs and return
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    public Page<SmsHistoryDTO> getPaginatedSmsHistory(int page, int size) {
        // Create a Pageable object for pagination, sorting by createdDate in descending order
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // Retrieve a page of entities from the repository
        Page<SmsHistoryEntity> entityPage = repository.findAll(pageable);

        List<SmsHistoryDTO> dtoList = entityPage.getContent().stream().map(entity -> toDto(entity)).collect(Collectors.toList());


        // Convert the Page of entities to a Page of DTOs
        return new PageImpl<>(dtoList, pageable, entityPage.getTotalElements());
    }

    public SmsHistoryDTO toDto(SmsHistoryEntity entity) {
        if (entity == null) {
            return null;
        }
        SmsHistoryDTO dto = new SmsHistoryDTO();
        dto.setId(entity.getId());
        dto.setPhone(entity.getToAccount());
        dto.setBody(entity.getBody());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
