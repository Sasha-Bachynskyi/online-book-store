package com.onlinebookstore.service.shoppingcart;

import com.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import com.onlinebookstore.model.User;

public interface ShoppingCartService {

    ShoppingCartDto getByUser(User user);
}
