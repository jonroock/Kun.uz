package dasturlash.uz.repository;

import dasturlash.uz.dto.FilterResultDTO;
import dasturlash.uz.dto.article.ArticleAdminFilterDTO;
import dasturlash.uz.dto.article.ArticleFilterDTO;
import dasturlash.uz.dto.article.CommentFilterDTO;
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
public class CommentCustomRepository {
    @Autowired
    private EntityManager entityManager;

    public FilterResultDTO<Object[]> filter(CommentFilterDTO filter, int page, int size) {

        StringBuilder selectQueryBuilder = new StringBuilder("select c.id, c.content, c.createdDate, c.updatedDate, c.likeCount, c.dislikeCount, c.replyId, " +
                " p.id,p.name,p.surname,p.photoId, " +
                " a.id, a.title " +
                " From CommentEntity c join c.profile as p " +
                " join c.article as a ");
        StringBuilder countQueryBuilder = new StringBuilder("select count(c) From CommentEntity c join c.profile as p join c.article as a ");

        StringBuilder builder = new StringBuilder(" where a.visible = true ");
        Map<String, Object> params = new HashMap<>();

        if (filter.getId() != null) {
            builder.append(" and  c.id =:id ");
            params.put("id", filter.getId());
        }
        if (filter.getArticleId() != null) {
            builder.append(" and  c.articleId =:articleId");
            params.put("articleId", filter.getArticleId());
        }
        if (filter.getProfileId() != null) {
            builder.append(" and  c.profileId =:profileId");
            params.put("profileId", filter.getProfileId());
        }

        if (filter.getCreatedDateTo() != null && filter.getCreatedDateFrom() != null) {
            builder.append(" and a.createdDate between :createdDateFrom and :createdDateTo ");
            params.put("createdDateFrom", LocalDateTime.of(filter.getCreatedDateFrom(), LocalTime.MIN));
            params.put("createdDateTo", LocalDateTime.of(filter.getCreatedDateTo(), LocalTime.MAX));
        } else if (filter.getCreatedDateFrom() != null) {
            builder.append(" and a.createdDate > :createdDateFrom ");
            params.put("createdDateFrom", LocalDateTime.of(filter.getCreatedDateFrom(), LocalTime.MIN));
        } else if (filter.getCreatedDateTo() != null) {
            builder.append(" and a.createdDate < :createdDateTo ");
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
}
