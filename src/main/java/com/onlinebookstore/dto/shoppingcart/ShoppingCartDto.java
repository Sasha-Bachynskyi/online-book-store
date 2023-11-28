package com.onlinebookstore.dto.shoppingcart;

import com.onlinebookstore.dto.cartitem.CartItemDto;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShoppingCartDto {
    private Long id;
    private Long userId;
    private List<CartItemDto> cartItemDtos;
}
