package dasturlash.uz.repository;

import dasturlash.uz.entity.EmailHistoryEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EmailHistoryRepository extends CrudRepository<EmailHistoryEntity, String>, PagingAndSortingRepository<EmailHistoryEntity, String> {


    Optional<EmailHistoryEntity> findTopByToAccountOrderByCreatedDateDesc(String account);

    @Query("from EmailHistoryEntity where toAccount = ?1 order by createdDate desc limit 1")
    Optional<EmailHistoryEntity> findLastByAccount(String account);

    List<EmailHistoryEntity> findByToAccount(String email);

    List<EmailHistoryEntity> findByCreatedDateBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
}
