package dasturlash.uz.repository;

import dasturlash.uz.entity.ProfileEntity;
import dasturlash.uz.enums.ProfileStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface ProfileRepository extends CrudRepository<ProfileEntity, Integer>, PagingAndSortingRepository<ProfileEntity, Integer> {

    // where username = ? and visible = true
    Optional<ProfileEntity> findByUsernameAndVisibleIsTrue(String username);

    Optional<ProfileEntity> findByIdAndVisibleIsTrue(Integer id);

    @Query("From ProfileEntity p inner join fetch p.roleList where p.visible = true")
    Page<ProfileEntity> findAllWithRoles(Pageable pageable);

    @Transactional
    @Modifying
    @Query("update ProfileEntity set status =?1 where username =?2")
    void setStatusByUsername(ProfileStatus status, String username);
}
