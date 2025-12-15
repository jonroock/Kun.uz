package dasturlash.uz.entity.article;

import dasturlash.uz.entity.AttachEntity;
import dasturlash.uz.entity.ProfileEntity;
import dasturlash.uz.entity.RegionEntity;
import dasturlash.uz.enums.ArticleStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "articles")
@Getter
@Setter
public class ArticleEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;


    @Column(name = "title", nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Column(name = "shared_count")
    private Long sharedCount= 0L;
    
    @Column(name = "image_id")
    private String imageId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id", insertable = false, updatable = false)
    private AttachEntity image;
    
    @Column(name = "region_id")
    private Integer regionId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", insertable = false, updatable = false)
    private RegionEntity region;

    @Column(name = "moderator_id")
    private Integer moderator_id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moderator_id",  insertable = false, updatable = false, nullable = false)
    private ProfileEntity moderator;

    @Column(name = "publisher_id")
    private Integer publisherId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id",  insertable = false, updatable = false, nullable = false)
    private ProfileEntity publisher;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status",nullable = false)
    private ArticleStatus status = ArticleStatus.NOT_PUBLISHED;
    
    @Column(name = "read_time")
    private Integer readTime; // in minutes
    
    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;
    
    @Column(name = "published_date")
    private LocalDateTime publishedDate;
    
    @Column
    private Boolean visible = true;
    
    @Column(name = "view_count")
    private Integer viewCount = 0;

}



