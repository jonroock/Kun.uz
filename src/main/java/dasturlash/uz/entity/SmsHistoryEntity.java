package dasturlash.uz.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
@Getter
@Setter
@Entity
@Table(name = "sms_history")
public class SmsHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "to_account")
    private String ToAccount;

    @Column(name = "body")
    private String body;

    @Column(name = "code")
    private Integer code;

    @Column(name = "phone")
    private String phone;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt ;
}
