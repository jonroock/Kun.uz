package dasturlash.uz.service;

import dasturlash.uz.dto.FilterResultDTO;
import dasturlash.uz.dto.RegionDTO;
import dasturlash.uz.dto.article.ArticleDTO;
import dasturlash.uz.dto.article.ArticleFilterDTO;
import dasturlash.uz.dto.article.CommentFilterDTO;
import dasturlash.uz.dto.comment.CommentCreateDTO;
import dasturlash.uz.dto.comment.CommentDTO;
import dasturlash.uz.dto.profile.ProfileDTO;
import dasturlash.uz.entity.CommentEntity;
import dasturlash.uz.entity.RegionEntity;
import dasturlash.uz.enums.AppLanguageEnum;
import dasturlash.uz.enums.ProfileRoleEnum;
import dasturlash.uz.exceptions.AppBadException;
import dasturlash.uz.mapper.CommentMapper;
import dasturlash.uz.mapper.RegionMapper;
import dasturlash.uz.repository.CommentCustomRepository;
import dasturlash.uz.repository.CommentRepository;
import dasturlash.uz.repository.RegionRepository;
import dasturlash.uz.util.SpringSecurityUtil;
import org.hibernate.annotations.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private AttachService attachService;
    @Autowired
    private CommentCustomRepository commentCustomRepository;

    public CommentDTO create(CommentCreateDTO dto) {
        CommentEntity entity = new CommentEntity();
        entity.setContent(dto.getContent());
        entity.setArticleId(dto.getArticleId());
        entity.setReplyId(dto.getReplyId());
        entity.setCreatedDate(LocalDateTime.now());
        entity.setProfileId(SpringSecurityUtil.currentProfileId());
        // save
        commentRepository.save(entity);
        // response
        return toDto(entity);
    }

    public CommentDTO update(String id, CommentCreateDTO dto) {
        CommentEntity entity = get(id);
        if (!entity.getProfileId().equals(SpringSecurityUtil.currentProfileId())) {
            throw new AppBadException("You can not update this article");
        }
        entity.setContent(dto.getContent());
        entity.setReplyId(dto.getReplyId());
        commentRepository.save(entity);
        return toDto(entity);
    }

    public Boolean delete(String id) {
        CommentEntity comment = get(id);
        if (!SpringSecurityUtil.hasAnyRoles(ProfileRoleEnum.ROLE_ADMIN)) {
            if (!comment.getProfileId().equals(SpringSecurityUtil.currentProfileId())) {
                throw new AppBadException("You can not delete this article");
            }
        }
        return commentRepository.updateVisibleById(id) == 1;
    }

    public List<CommentDTO> commentRepliedList(String commentId) {
        List<CommentMapper> pageObj = commentRepository.repliedCommentList(commentId);
        List<CommentDTO> dtoList = new LinkedList<>();
        pageObj.forEach(mapper -> dtoList.add(toDto(mapper)));
        return dtoList;
    }

    public Page<CommentDTO> filter(CommentFilterDTO filter, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        FilterResultDTO<Object[]> filterResult = commentCustomRepository.filter(filter, page, size);
        List<CommentDTO> commentList = new LinkedList<>();
        for (Object[] obj : filterResult.getContent()) {
            CommentDTO comment = new CommentDTO();
            comment.setId((String) obj[0]);
            comment.setContent((String) obj[1]);
            comment.setCreatedDate((LocalDateTime) obj[2]);
            comment.setUpdatedDate((LocalDateTime) obj[3]);
            comment.setLikeCount((Long) obj[4]);
            comment.setDisLikeCount((Long) obj[5]);
            if (obj[6] != null) {
                CommentDTO reply = new CommentDTO();
                reply.setId((String) obj[6]);
                comment.setReply(reply);
            }

            ProfileDTO profile = new ProfileDTO();
            profile.setId((Integer) obj[7]);
            profile.setName((String) obj[8]);
            profile.setSurname((String) obj[9]);
            if (obj[10] != null) {
                profile.setPhoto(attachService.openDTO((String) obj[10]));
            }
            ArticleDTO article = new ArticleDTO();
            article.setId((String) obj[11]);
            article.setTitle((String) obj[12]);
            comment.setArticle(article);

            commentList.add(comment);
        }
        return new PageImpl<>(commentList, PageRequest.of(page, size), filterResult.getTotal());
    }

    public Page<CommentDTO> getAllByArticleId(String articleId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<CommentMapper> pageObj = commentRepository.findByArticleId(articleId, pageRequest);
        List<CommentDTO> dtos = new LinkedList<>();
        pageObj.forEach(mapper -> dtos.add(toDto(mapper)));
        return new PageImpl<>(dtos, pageRequest, pageObj.getTotalElements());
    }

    private CommentDTO toDto(CommentEntity entity) {
        CommentDTO dto = new CommentDTO();
        dto.setId(entity.getId());
        dto.setContent(entity.getContent());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setUpdatedDate(entity.getUpdatedDate());
        if (entity.getReplyId() != null) {
            CommentDTO reply = new CommentDTO();
            reply.setId(entity.getId());
            dto.setReply(reply);
        }
//        dto.setProfile();
        return dto;
    }

    private CommentDTO toDto(CommentMapper mapper) {
        CommentDTO dto = new CommentDTO();
        dto.setId(mapper.getId());
        dto.setContent(mapper.getContent());
        dto.setCreatedDate(mapper.getCreatedDate());
        dto.setUpdatedDate(mapper.getUpdatedDate());
        dto.setLikeCount(mapper.getLikeCount());
        dto.setDisLikeCount(mapper.getLikeCount());

        ProfileDTO profile = new ProfileDTO();
        profile.setId(mapper.getProfileId());
        profile.setName(mapper.getProfileName());
        profile.setSurname(mapper.getProfileSurname());
        if (mapper.getProfilePhotoId() != null) {
            profile.setPhoto(attachService.openDTO(mapper.getProfilePhotoId()));
        }
        dto.setProfile(profile);

        return dto;
    }

    public CommentEntity get(String id) {
        return commentRepository.findById(id).orElseThrow(() -> {
            throw new AppBadException("Item not found");
        });
    }
}
