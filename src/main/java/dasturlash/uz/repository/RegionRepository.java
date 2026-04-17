package dasturlash.uz.repository;

import dasturlash.uz.entity.CategoryEntity;
import dasturlash.uz.entity.RegionEntity;
import dasturlash.uz.enums.AppLanguageEnum;
import dasturlash.uz.mapper.CategoryMapper;
import dasturlash.uz.mapper.RegionMapper;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public interface RegionRepository extends CrudRepository<RegionEntity, Integer> {

    Optional<RegionEntity> findByIdAndVisibleIsTrue(Integer id);

    @Transactional
    @Modifying
    @Query("update RegionEntity set visible = false where id = ?1")
    int updateVisibleById(Integer id);

    @Query("from RegionEntity where visible = true order by orderNumber")
    Iterable<RegionEntity> findAll();


    @Query(value = "SELECT c.id AS id, " +
            "CASE :lang " +
            "   WHEN 'UZ' THEN c.name_uz " +
            "   WHEN 'RU' THEN c.name_ru " +
            "   WHEN 'EN' THEN c.name_en " +
            "END AS name, " +
            "c.order_number AS orderNumber, " +
            "c.region_key AS regionKey " +
            "FROM region c " +
            "WHERE c.visible = true order by order_number asc", nativeQuery = true)
    List<Object[]> getByLangNative(@Param("lang") String lang);

    @Query("SELECT c.id AS id, " +
            "CASE :lang " +
            "   WHEN 'UZ' THEN c.nameUz " +
            "   WHEN 'RU' THEN c.nameRu " +
            "   WHEN 'EN' THEN c.nameEn " +
            "END AS name, " +
            "c.orderNumber AS orderNumber, " +
            "c.regionKey AS regionKey " +
            "FROM RegionEntity c " +
            "WHERE c.visible = true order by orderNumber asc")
    List<RegionMapper> getByLang(@Param("lang") String lang);

    @Query("SELECT c.id AS id, " +
            "CASE :lang " +
            "   WHEN 'UZ' THEN c.nameUz " +
            "   WHEN 'RU' THEN c.nameRu " +
            "   WHEN 'EN' THEN c.nameEn " +
            "END AS name, " +
            "c.regionKey AS regionKey " +
            "FROM RegionEntity c " +
            "WHERE c.visible = true and c.id = :id")
    Optional<RegionMapper> getByIdAndLang(@Param("id") Integer id, @Param("lang") String lang);

    Optional<RegionEntity> findByRegionKey(String key);
}
