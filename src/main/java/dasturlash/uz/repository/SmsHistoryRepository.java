package dasturlash.uz.repository;

import dasturlash.uz.entity.EmailHistoryEntity;
import dasturlash.uz.entity.SmsHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SmsHistoryRepository extends PagingAndSortingRepository<SmsHistoryEntity, Integer> { 

    Optional<SmsHistoryEntity> findFirstByPhoneOrderByCreatedAtDesc(String account);


    List<SmsHistoryEntity> findByPhoneOrderByCreatedAtDesc(String account);

    List<SmsHistoryEntity> findByPhoneAndCreatedAtAfterOrderByCreatedAtDesc(String phone, LocalDateTime from);

    void save(SmsHistoryEntity entity);
}
