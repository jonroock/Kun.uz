package dasturlash.uz.mapper;

import java.time.LocalDateTime;

public interface ProfileFilterMapper {
    Integer getId();
    String getName();
    String getSurname();
    String getUsername();
    String getSatus();
    LocalDateTime getCreatedDate();
    Object [] getRoles();
}
