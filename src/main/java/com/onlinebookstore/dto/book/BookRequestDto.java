package com.onlinebookstore.dto.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookRequestDto {
    @NotBlank
    private String title;
    @NotBlank
    private String author;
    @NotBlank
    private String isbn;
    @Min(0)
    private BigDecimal price;
    private String description;
    private String coverImage;
    private Set<Long> categoryIds;
}
