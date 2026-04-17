package dasturlash.uz.entity;

import dasturlash.uz.enums.ProfileStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "profile")
public class ProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "surname", nullable = false)
    private String surname;

    @Column(name = "username", nullable = false)
    private String username;// 1213

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ProfileStatus status;

    @Column(name = "photo_id")
    private String photoId; // Will do it later (in attach topic)

    @Column(name = "visible", nullable = false)
    private Boolean visible = true;

    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @OneToMany(mappedBy = "profile")
    private List<ProfileRoleEntity> roleList;
}