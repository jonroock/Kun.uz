package dasturlash.uz.service.email;

import dasturlash.uz.dto.EmailHistoryDTO;
import dasturlash.uz.entity.EmailHistoryEntity;
import dasturlash.uz.repository.EmailHistoryRepository;
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
public class EmailHistoryService {
    @Autowired
    private EmailHistoryRepository emailHistoryRepository;

    public void create(String body, int code, String toAccount) {
        EmailHistoryEntity entity = new EmailHistoryEntity();
        entity.setBody(body);
        entity.setCode(code);
        entity.setToAccount(toAccount);
        emailHistoryRepository.save(entity);
    }

    public boolean isSmsSendToAccount(String account, int code) {
        Optional<EmailHistoryEntity> optional = emailHistoryRepository.findLastByAccount(account);
        if (optional.isEmpty()) {
            return false;
        }
        EmailHistoryEntity entity = optional.get();
        if (!entity.getCode().equals(code)) {
            return false;
        }
        //  20:32:40           =   20:30.40  + 0:2:00
        LocalDateTime extDate = entity.getCreatedDate().plusMinutes(2);
        // now  20:31:30  >  20:32:40    |     now 20:35:30  >  20:32:40
        if (LocalDateTime.now().isAfter(extDate)) {
            return false;
        }
        return true;
    }

    public List<EmailHistoryDTO> getEmailHistoryByEmail(String email) {
        // Find entities by toAccount (which is 'email' in the DTO context)
        List<EmailHistoryEntity> entities = emailHistoryRepository.findByToAccount(email);
        // Convert entities to DTOs and return
        return entities.stream().map(entity -> toDto(entity)).collect(Collectors.toList());
    }

    public List<EmailHistoryDTO> getEmailHistoryByDate(LocalDate date) {
        // Calculate the start and end of the given day
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX); // Represents 23:59:59.999999999

        // Find entities created within the specified date range
        List<EmailHistoryEntity> entities = emailHistoryRepository.findByCreatedDateBetween(startOfDay, endOfDay);
        // Convert entities to DTOs and return
        return entities.stream().map(entity -> toDto(entity)).collect(Collectors.toList());
    }

    public Page<EmailHistoryDTO> getPaginatedEmailHistory(int page, int size) {
        // Create a Pageable object for pagination, sorting by createdDate in descending order
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());

        // Retrieve a page of entities from the repository
        Page<EmailHistoryEntity> entityPage = emailHistoryRepository.findAll(pageable);

        List<EmailHistoryDTO> dtoList = entityPage.getContent().stream().map(entity -> toDto(entity)).collect(Collectors.toList());


        // Convert the Page of entities to a Page of DTOs
        return new PageImpl<>(dtoList, pageable, entityPage.getTotalElements());
    }

    public EmailHistoryDTO toDto(EmailHistoryEntity entity) {
        if (entity == null) {
            return null;
        }
        EmailHistoryDTO dto = new EmailHistoryDTO();
        dto.setId(entity.getId());
        dto.setEmail(entity.getToAccount());
        dto.setBody(entity.getBody());
        dto.setCreatedDate(entity.getCreatedDate());
        return dto;
    }

}
