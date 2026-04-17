package dasturlash.uz.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RegionDTO {
    private Integer id;

    @NotNull(message = "OrderNumber required")
    @Min(value = 1, message = "OrderNumber have to higher than 0")
    private Integer orderNumber;

    @NotBlank(message = "NameUz required")
    private String nameUz;

    @NotBlank(message = "NameRu required")
    private String nameRu;

    @NotBlank(message = "NameEn required")
    private String nameEn;

    @NotBlank(message = "RegionKey required")
    private String regionKey;

    private LocalDateTime createdDate;
    private String name;
}
