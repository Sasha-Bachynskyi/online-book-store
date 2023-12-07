package com.onlinebookstore.mapper;

import com.onlinebookstore.config.MapperConfig;
import com.onlinebookstore.dto.cartitem.CartItemDto;
import com.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import com.onlinebookstore.model.CartItem;
import com.onlinebookstore.model.ShoppingCart;
import java.util.List;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.util.CollectionUtils;

@Mapper(config = MapperConfig.class)
public interface ShoppingCartMapper {
    @Mapping(source = "user.id", target = "userId")
    ShoppingCartDto toDto(ShoppingCart shoppingCart);

    @AfterMapping
    default void toCartItemsDto(@MappingTarget ShoppingCartDto shoppingCartDto,
                                ShoppingCart shoppingCart) {
        if (!CollectionUtils.isEmpty(shoppingCart.getCartItems())) {
            List<CartItemDto> cartItemDtos = shoppingCart.getCartItems().stream()
                    .map(this::toCartItemDto)
                    .toList();
            shoppingCartDto.setCartItemDtos(cartItemDtos);
        }
    }

    private CartItemDto toCartItemDto(CartItem cartItem) {
        CartItemDto dto = new CartItemDto();
        dto.setId(cartItem.getId());
        dto.setBookId(cartItem.getBook().getId());
        dto.setBookTitle(cartItem.getBook().getTitle());
        dto.setQuantity(cartItem.getQuantity());
        return dto;
    }
}
