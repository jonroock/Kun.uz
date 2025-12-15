package dasturlash.uz.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttachDTO {

    private String id;
    private String original_name;
    private String path;
    private Integer size;
    private String extension;
    private LocalDate createdDated;
}
