package dasturlash.uz.service;

import dasturlash.uz.dto.SectionDTO;
import dasturlash.uz.entity.ArticleSectionEntity;
import dasturlash.uz.repository.ArticleSectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleSectionService {
    @Autowired
    private ArticleSectionRepository articleSectionRepository;

    public void merge(String articleId, List<SectionDTO> dtoList) {
        List<Integer> newList = dtoList.stream().map(SectionDTO::getId).toList();

        List<Integer> oldList = articleSectionRepository.getCategoryIdListByArticleId(articleId);
        newList.stream().filter(n -> !oldList.contains(n)).forEach(pe -> create(articleId, pe)); // create
        oldList.stream().filter(old -> !newList.contains(old)).forEach(pe -> articleSectionRepository.deleteByArticleIdAndSectionId(articleId, pe));
    }

    public void create(String articleId, Integer categoryId) {
        ArticleSectionEntity entity = new ArticleSectionEntity();
        entity.setArticleId(articleId);
        entity.setSectionId(categoryId);
        articleSectionRepository.save(entity);
    }
}
