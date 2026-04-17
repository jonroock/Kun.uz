package dasturlash.uz.repository;

import dasturlash.uz.dto.FilterResultDTO;
import dasturlash.uz.dto.profile.ProfileFilterDTO;
import dasturlash.uz.entity.ProfileEntity;
import dasturlash.uz.mapper.ProfileFilterMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ProfileCustomRepository {

    @Autowired
    private EntityManager entityManager;

    public FilterResultDTO<Object[]> filter(ProfileFilterDTO filter, int page, int size) {

        StringBuilder condition = new StringBuilder(" where p.visible = true ");
        Map<String, Object> params = new HashMap<>();
        if (filter.getQuery() != null) {
            condition.append(" and (lower(p.name) like :query or lower(p.surname) like :query or lower(p.username) like :query) ");
            params.put("query", "%" + filter.getQuery().toLowerCase() + "%");
        }
        if (filter.getRole() != null) { // ROLE_ADMIN
            condition.append(" and  EXISTS ( SELECT 1 FROM profile_role pr WHERE pr.profile_id = p.id and pr.roles = :role) ");
            params.put("role", filter.getRole().name());
        }
        if (filter.getCreatedDateFrom() != null && filter.getCreatedDateTo() != null) { // 01.01.2021 - 01.01.2024
            condition.append(" and p.created_date between :fromDate  and :toDate ");
            params.put("fromDate", LocalDateTime.of(filter.getCreatedDateFrom(), LocalTime.MIN));
            params.put("toDate", LocalDateTime.of(filter.getCreatedDateTo(), LocalTime.MAX));
        } else if (filter.getCreatedDateFrom() != null) { // 01.01.2021
            condition.append(" and p.created_date >= :fromDate");
            params.put("fromDate", LocalDateTime.of(filter.getCreatedDateFrom(), LocalTime.MIN));
        } else if (filter.getCreatedDateTo() != null) { // 01.01.2024
            condition.append(" and p.created_date <= :toDate ");
            params.put("toDate", LocalDateTime.of(filter.getCreatedDateTo(), LocalTime.MAX));
        }
        //
        StringBuilder selectBuilder = new StringBuilder("Select p.id as id,p.name as name,p.surname as surname," +
                " p.username as username, p.status as status, p.created_date as createdDate," +
                " ARRAY_AGG(pr.roles) AS roles  " +
                " From profile p ");
        selectBuilder.append(" left join profile_role as pr on pr.profile_id = p.id ");

        StringBuilder countBuilder = new StringBuilder("Select count(*) From profile p ");

        // conditions
        selectBuilder.append(condition);
        countBuilder.append(condition);
        // group by
        selectBuilder.append(" GROUP BY p.id, p.name, p.surname, p.username, p.status, p.created_date ");
        countBuilder.append(" GROUP BY p.id, p.name, p.surname, p.username, p.status, p.created_date ");
        //
        Query selectQuery = entityManager.createNativeQuery(selectBuilder.toString());
        Query countQuery = entityManager.createNativeQuery(countBuilder.toString());

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            selectQuery.setParameter(entry.getKey(), entry.getValue());
            countQuery.setParameter(entry.getKey(), entry.getValue());
        }

        selectQuery.setMaxResults(size); // limit
        selectQuery.setFirstResult(page * size); // offset

        List<Object[]> profileList = selectQuery.getResultList();
        Long totalCount = (Long) countQuery.getSingleResult();

        return new FilterResultDTO<>(profileList, totalCount);
    }

    // the following strategy also can be used. Bu role larni bitta String ("ROLE_USER,ROLE_ADMIN") qilib jo'natadi.
    // (select string_agg(r.roles,',') from profile_role as r where r.profile_id = 1 ) as roles
    // ARRAY_AGG(pr.roles) AS roles  - bu array ([ROLE_USER,ROLE_ADMIN]) qilib jo'natadi.
}

