package dasturlash.uz.repository;

import dasturlash.uz.dto.FilterResultDTO;
import dasturlash.uz.dto.article.ArticleAdminFilterDTO;
import dasturlash.uz.dto.article.ArticleFilterDTO;
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
public class ArticleCustomRepository {
    @Autowired
    private EntityManager entityManager;

    public FilterResultDTO<Object[]> filter(ArticleFilterDTO filter, int page, int size, Boolean isModerator) {

        StringBuilder selectQueryBuilder = new StringBuilder("SELECT a.id, a.title, a.description, a.publishedDate,a.imageId FROM ArticleEntity a ");
        // 1 - N
        StringBuilder countQueryBuilder = new StringBuilder("SELECT count(a) FROM ArticleEntity a ");

        StringBuilder builder = new StringBuilder(" where a.visible = true ");
        if (!isModerator) { // if not moderator add status = 'PUBLISHED' condition
            builder.append("and a.status = 'PUBLISHED'");
        }

        Map<String, Object> params = new HashMap<>();

        if (filter.getTitle() != null) {
            builder.append(" and  lower(a.title) like :title ");
            params.put("title", "%" + filter.getTitle().toLowerCase() + "%");
        }
        if (filter.getRegionId() != null) {
            builder.append(" and  a.regionId =:regionId");
            params.put("regionId", filter.getRegionId());
        }
        if (filter.getSectionId() != null) {
            selectQueryBuilder.append(" inner join ArticleSectionEntity ase on ase.articleId = a.id  ");
            countQueryBuilder.append(" inner join ArticleSectionEntity ase on ase.articleId = a.id  ");

            builder.append(" and  ase.sectionId =:sectionId");
            params.put("sectionId", filter.getSectionId());
        }
        if (filter.getCategoryId() != null) {
            selectQueryBuilder.append(" inner join ArticleCategoryEntity ace on ace.categoryId = a.id  ");
            countQueryBuilder.append(" inner join ArticleCategoryEntity ace on ace.categoryId = a.id  ");

            builder.append(" and  ace.categoryId =:categoryId");
            params.put("categoryId", filter.getCategoryId());
        }
        if (filter.getCreatedDateTo() != null && filter.getCreatedDateFrom() != null) {
            builder.append(" and a.publishedDate between :createdDateFrom and :createdDateTo ");
            params.put("createdDateFrom", LocalDateTime.of(filter.getCreatedDateFrom(), LocalTime.MIN));
            params.put("createdDateTo", LocalDateTime.of(filter.getCreatedDateTo(), LocalTime.MAX));
        } else if (filter.getCreatedDateFrom() != null) {
            builder.append(" and a.publishedDate > :createdDateFrom ");
            params.put("createdDateFrom", LocalDateTime.of(filter.getCreatedDateFrom(), LocalTime.MIN));
        } else if (filter.getCreatedDateTo() != null) {
            builder.append(" and a.publishDate < :createdDateTo ");
            params.put("publishedDate", LocalDateTime.of(filter.getCreatedDateTo(), LocalTime.MAX));
        }

        selectQueryBuilder.append(builder);
        countQueryBuilder.append(builder);

        // select query
        Query selectQuery = entityManager.createQuery(selectQueryBuilder.toString());
        selectQuery.setFirstResult((page) * size); // 50
        selectQuery.setMaxResults(size); // 30
        params.forEach(selectQuery::setParameter);

        List<Object[]> articleList = selectQuery.getResultList();

        // totalCount query
        Query countQuery = entityManager.createQuery(countQueryBuilder.toString());
        params.forEach(countQuery::setParameter);

        Long totalElements = (Long) countQuery.getSingleResult();

        return new FilterResultDTO<Object[]>(articleList, totalElements);
    }

    public FilterResultDTO<Object[]> filterAsAdmin(ArticleAdminFilterDTO filter, int page, int size) {

        StringBuilder selectQueryBuilder = new StringBuilder("SELECT a.id, a.title, a.description, a.createdDate, a.publishedDate, a.imageId, a.status FROM ArticleEntity a ");
        // 1 - N
        StringBuilder countQueryBuilder = new StringBuilder("SELECT count(a) FROM ArticleEntity a ");

        StringBuilder builder = new StringBuilder(" where a.visible = true ");

        Map<String, Object> params = new HashMap<>();

        if (filter.getTitle() != null) {
            builder.append(" and  lower(a.title) like :title ");
            params.put("title", "%" + filter.getTitle().toLowerCase() + "%");
        }
        if (filter.getRegionId() != null) {
            builder.append(" and  a.regionId =:regionId");
            params.put("regionId", filter.getRegionId());
        }
        if (filter.getSectionId() != null) {
            selectQueryBuilder.append(" inner join ArticleSectionEntity ase on ase.articleId = a.id  ");
            countQueryBuilder.append(" inner join ArticleSectionEntity ase on ase.articleId = a.id  ");

            builder.append(" and  ase.sectionId =:sectionId");
            params.put("sectionId", filter.getSectionId());
        }
        if (filter.getCategoryId() != null) {
            selectQueryBuilder.append(" inner join ArticleCategoryEntity ace on ace.categoryId = a.id  ");
            countQueryBuilder.append(" inner join ArticleCategoryEntity ace on ace.categoryId = a.id  ");

            builder.append(" and  ace.categoryId =:categoryId");
            params.put("categoryId", filter.getCategoryId());
        }
        // created_date
        if (filter.getCreatedDateTo() != null && filter.getCreatedDateFrom() != null) {
            builder.append(" and a.createdDate between :createdDateFrom and :createdDateTo ");
            params.put("createdDateFrom", LocalDateTime.of(filter.getCreatedDateFrom(), LocalTime.MIN));
            params.put("createdDateTo", LocalDateTime.of(filter.getCreatedDateTo(), LocalTime.MAX));
        } else if (filter.getCreatedDateFrom() != null) {
            builder.append(" and a.createdDate > :createdDateFrom ");
            params.put("createdDateFrom", LocalDateTime.of(filter.getCreatedDateFrom(), LocalTime.MIN));
        } else if (filter.getCreatedDateTo() != null) {
            builder.append(" and a.createdDate < :createdDateTo ");
            params.put("createdDateTo", LocalDateTime.of(filter.getCreatedDateTo(), LocalTime.MAX));
        }
        // published_date
        if (filter.getPublishedDateFrom() != null && filter.getPublishedDateTo() != null) {
            builder.append(" and a.publishedDate between :publishedDateFrom and :publishedDateTo ");
            params.put("publishedDateFrom", LocalDateTime.of(filter.getPublishedDateFrom(), LocalTime.MIN));
            params.put("publishedDateTo", LocalDateTime.of(filter.getPublishedDateTo(), LocalTime.MAX));
        } else if (filter.getPublishedDateFrom() != null) {
            builder.append(" and a.publishedDate > :publishedDateFrom ");
            params.put("publishedDateFrom", LocalDateTime.of(filter.getPublishedDateFrom(), LocalTime.MIN));
        } else if (filter.getPublishedDateTo() != null) {
            builder.append(" and a.publishedDate < :publishedDateTo ");
            params.put("publishedDateTo", LocalDateTime.of(filter.getPublishedDateTo(), LocalTime.MAX));
        }
        if (filter.getModeratorId() != null) {
            builder.append(" and  a.moderatorId =:moderatorId");
            params.put("moderatorId", filter.getModeratorId());
        }
        if (filter.getPublisherId() != null) {
            builder.append(" and  a.publisherId =:publisherId");
            params.put("publisherId", filter.getPublisherId());
        }
        if (filter.getStatus() != null) {
            builder.append("and a.status =:status");
            params.put("status", filter.getStatus());
        }

        selectQueryBuilder.append(builder);
        countQueryBuilder.append(builder);

        // select query
        Query selectQuery = entityManager.createQuery(selectQueryBuilder.toString());
        selectQuery.setFirstResult((page) * size); // 50
        selectQuery.setMaxResults(size); // 30
        params.forEach(selectQuery::setParameter);

        List<Object[]> articleList = selectQuery.getResultList();

        // totalCount query
        Query countQuery = entityManager.createQuery(countQueryBuilder.toString());
        params.forEach(countQuery::setParameter);

        Long totalElements = (Long) countQuery.getSingleResult();

        return new FilterResultDTO<Object[]>(articleList, totalElements);
    }

}
