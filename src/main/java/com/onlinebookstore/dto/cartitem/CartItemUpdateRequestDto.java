package com.onlinebookstore.dto.cartitem;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemUpdateRequestDto {
    @Nonnull
    @Min(1)
    private Integer quantity;
}
