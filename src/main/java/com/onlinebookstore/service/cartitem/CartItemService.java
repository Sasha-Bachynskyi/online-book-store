package com.onlinebookstore.service.cartitem;

import com.onlinebookstore.dto.cartitem.CartItemDto;
import com.onlinebookstore.dto.cartitem.CartItemRequestDto;
import com.onlinebookstore.dto.cartitem.CartItemUpdateRequestDto;

public interface CartItemService {
    CartItemDto save(Long userId, CartItemRequestDto requestDto);

    void update(Long id, CartItemUpdateRequestDto requestDto);

    void deleteById(Long id);
}
