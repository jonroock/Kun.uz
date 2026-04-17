package dasturlash.uz.repository.sms;

import dasturlash.uz.entity.sms.SmsTokenEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SmsTokenRepository extends CrudRepository<SmsTokenEntity, Integer> {
    Optional<SmsTokenEntity> findTopByOrderByCreatedDateDesc();
}
