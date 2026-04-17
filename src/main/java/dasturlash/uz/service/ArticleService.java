package dasturlash.uz.service;

import dasturlash.uz.dto.FilterResultDTO;
import dasturlash.uz.dto.article.ArticleAdminFilterDTO;
import dasturlash.uz.dto.article.ArticleCreateDTO;
import dasturlash.uz.dto.article.ArticleDTO;
import dasturlash.uz.dto.article.ArticleFilterDTO;
import dasturlash.uz.entity.ArticleEntity;
import dasturlash.uz.enums.AppLanguageEnum;
import dasturlash.uz.enums.ArticleStatus;
import dasturlash.uz.exceptions.AppBadException;
import dasturlash.uz.mapper.ArticleShortInfo;
import dasturlash.uz.repository.ArticleCustomRepository;
import dasturlash.uz.repository.ArticleRepository;
import dasturlash.uz.util.SpringSecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private ArticleCategoryService articleCategoryService;

    @Autowired
    private ArticleSectionService articleSectionService;
    @Autowired
    private SectionService sectionService;

    @Autowired
    private AttachService attachService;
    @Autowired
    private RegionService regionService;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ArticleCustomRepository articleCustomRepository;


    // 1
    public ArticleDTO create(ArticleCreateDTO createDTO) {
        ArticleEntity entity = new ArticleEntity();
        toEntity(createDTO, entity);
        // Set default values
        entity.setStatus(ArticleStatus.NOT_PUBLISHED);
        entity.setCreatedDate(LocalDateTime.now());
        entity.setVisible(true);
        entity.setViewCount(0);
        entity.setSharedCount(0L);
        entity.setModeratorId(SpringSecurityUtil.currentProfileId());
        // save
        articleRepository.save(entity);
        // category -> merge
        articleCategoryService.merge(entity.getId(), createDTO.getCategoryList());
        // section -> merge
        articleSectionService.merge(entity.getId(), createDTO.getSectionList());
        // return
        return toDTO(entity);
    }

    // 2
    public ArticleDTO update(String articleId, ArticleCreateDTO createDTO) {
        ArticleEntity entity = get(articleId);
        toEntity(createDTO, entity);
        // update
        articleRepository.save(entity);
        // category -> merge
        articleCategoryService.merge(entity.getId(), createDTO.getCategoryList());
        // section -> merge
        articleSectionService.merge(entity.getId(), createDTO.getSectionList());
        // return
        return toDTO(entity);
    }

    // 3
    public String delete(String articleId) {
        int effectedRows = articleRepository.delete(articleId);
        if (effectedRows > 0) {
            return "Article deleted";
        } else {
            return "Something went wrong";
        }
//        ArticleEntity entity = get(articleId);
//        entity.setVisible(Boolean.FALSE);
//        articleRepository.save(entity);
//        return "Article deleted";
    }

    // 4
    public String changeStatus(String articleId, ArticleStatus status) {
        int effectedRows = articleRepository.changeStatus(articleId, status);
        if (effectedRows > 0) {
            return "Article status change";
        } else {
            return "Something went wrong";
        }
//        ArticleEntity entity = get(articleId);
//        entity.setStatus(status);
//        articleRepository.save(entity);
//        return "Article status changed";
    }

    // 5 -
    public List<ArticleDTO> getBySectionId(Integer sectionId, int limit) { // 1, 100
        List<ArticleShortInfo> resultList = articleRepository.getBySectionId(sectionId, limit);
        List<ArticleDTO> responseList = new LinkedList<>();
        resultList.forEach(mapper -> responseList.add(toDTO(mapper)));
        return responseList;
    }

    //  6
    public List<ArticleDTO> getLast12ArticleExcept(List<String> exceptIdList) { // 1, 100
        List<ArticleShortInfo> resultList = articleRepository.getLastArticleListExceptGiven(exceptIdList);
        List<ArticleDTO> responseList = new LinkedList<>();
        resultList.forEach(mapper -> responseList.add(toDTO(mapper)));
        return responseList;
    }

    // 7
    public List<ArticleDTO> getLastNArticleByCategoryId(Integer categoryId, int limit) {
        List<ArticleShortInfo> resultList = articleRepository.getLastNByCategoryId(categoryId, limit);
        List<ArticleDTO> responseList = new LinkedList<>();
        resultList.forEach(mapper -> responseList.add(toDTO(mapper)));
        return responseList;
    }

    // 8
    public List<ArticleDTO> getLastNArticleByRegionId(Integer regionId, Integer limit) {
        List<ArticleShortInfo> resultList = articleRepository.getLastNByRegionId(regionId, limit);
        List<ArticleDTO> responseList = new LinkedList<>();
        resultList.forEach(mapper -> responseList.add(toDTO(mapper)));
        return responseList;
    }

    //   12. Get 4 most read articles, except given article id .
    public List<ArticleDTO> getMostRead4ArticleExceptGivenId(String exceptArticleId) {
        List<ArticleShortInfo> resultList = articleRepository.mostRead4Article(exceptArticleId);
        List<ArticleDTO> responseList = new LinkedList<>();
        resultList.forEach(mapper -> responseList.add(toDTO(mapper)));
        return responseList;
    }

    //  13. Increase Article View Count by Article Id
    public Boolean increaseViewCount(String articleId) {
        // Qaysi IP orqali view qilinganini yozib qo'ysak yaxshi bo'lar edi, Shu IP dan shu maqolani o'qishdi deb.
        articleRepository.increaseViewCount(articleId);
        return Boolean.TRUE;
    }

    //  14. Increase Article Shared Count by Article Id
    public Integer increaseSharedCount(String articleId) {
        int sharedCount = articleRepository.incrementSharedCountAndGet(articleId);
        return sharedCount;
    }

    // 15
    public Page<ArticleDTO> filter(ArticleFilterDTO filter, int page, int size, Boolean isModerator) { // 1, 100
        FilterResultDTO<Object[]> filterResult = articleCustomRepository.filter(filter, page, size, isModerator);
        List<ArticleDTO> articleList = new LinkedList<>();
        for (Object[] obj : filterResult.getContent()) {
            ArticleDTO article = new ArticleDTO();
            // a.id, a.title, a.description, a.publishedDate,a.imageId
            article.setId((String) obj[0]);
            article.setTitle((String) obj[1]);
            article.setDescription((String) obj[2]);
            article.setPublishedDate((LocalDateTime) obj[3]);
            if (obj[4] != null) {
                article.setImage(attachService.openDTO((String) obj[4]));
            }
            articleList.add(article);
        }
        return new PageImpl<>(articleList, PageRequest.of(page, size), filterResult.getTotal());
    }

    // 17 filter as admin
    public Page<ArticleDTO> filterAsAdmin(ArticleAdminFilterDTO filter, int page, int size) { // 1, 100
        FilterResultDTO<Object[]> filterResult = articleCustomRepository.filterAsAdmin(filter, page, size);
        List<ArticleDTO> articleList = new LinkedList<>();
        for (Object[] obj : filterResult.getContent()) {
            ArticleDTO article = new ArticleDTO();
            // a.id, a.title, a.description, a.publishedDate,a.imageId
            article.setId((String) obj[0]);
            article.setTitle((String) obj[1]);
            article.setDescription((String) obj[2]);
            article.setCreatedDate((LocalDateTime) obj[3]);
            article.setPublishedDate((LocalDateTime) obj[4]);
            if (obj[5] != null) {
                article.setImage(attachService.openDTO((String) obj[5]));
            }
            if (obj[6] != null) {
                article.setStatus((ArticleStatus) obj[6]);
            }
            articleList.add(article);
        }
        return new PageImpl<>(articleList, PageRequest.of(page, size), filterResult.getTotal());
    }

    public ArticleDTO getById(String id, AppLanguageEnum lang) {
        // get
        ArticleEntity entity = get(id);

        ArticleDTO dto = new ArticleDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setContent(entity.getContent());
        dto.setSharedCount(entity.getSharedCount());
        dto.setReadTime(entity.getReadTime());
        dto.setViewCount(entity.getViewCount());
        dto.setPublishedDate(entity.getPublishedDate());
//        dto.setLikeCount(entity.getLikeCount());
        // articleLikeService.getArticleLikeCount(entity.getId()); select count(*) from article_lik where article_id = ? and emotion = 'KILE';
        // set region
        dto.setRegion(regionService.getByIdAndLang(entity.getRegionId(), lang));
        // set section   ->  1 -N
        dto.setSectionList(sectionService.getSectionListByArticleIdAndLang(entity.getId(), lang));
        // category
        dto.setCategoryList(categoryService.getCategoryListByArticleIdAndLang(entity.getId(), lang));
        // tag
        return dto;
    }

    private void toEntity(ArticleCreateDTO dto, ArticleEntity entity) {
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setContent(dto.getContent());
        entity.setImageId(dto.getImageId());
        entity.setRegionId(dto.getRegionId());
        entity.setReadTime(dto.getReadTime());
    }

    private ArticleDTO toDTO(ArticleEntity entity) {
        ArticleDTO dto = new ArticleDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setContent(entity.getContent());
        dto.setSharedCount(entity.getSharedCount());
        dto.setReadTime(entity.getReadTime());
        dto.setViewCount(entity.getViewCount());
        dto.setStatus(entity.getStatus());
        dto.setImageId(entity.getImageId());
        dto.setRegionId(entity.getRegionId());
        dto.setPublishedDate(entity.getPublishedDate());
        return dto;
    }

    private ArticleDTO toDTO(ArticleShortInfo mapper) {
        ArticleDTO dto = new ArticleDTO();
        dto.setId(mapper.getId());
        dto.setTitle(mapper.getTitle());
        dto.setDescription(mapper.getDescription());
        dto.setImage(attachService.openDTO(mapper.getId()));
        dto.setPublishedDate(mapper.getPublishedDate());
        return dto;
    }

    public ArticleEntity get(String id) {
        return articleRepository.findById(id).orElseThrow(() -> {
            throw new AppBadException("Article not found");
        });
    }

}
