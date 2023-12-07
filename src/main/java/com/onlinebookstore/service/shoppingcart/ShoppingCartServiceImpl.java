package com.onlinebookstore.service.shoppingcart;

import com.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import com.onlinebookstore.mapper.ShoppingCartMapper;
import com.onlinebookstore.model.ShoppingCart;
import com.onlinebookstore.model.User;
import com.onlinebookstore.repository.shoppingcart.ShoppingCartRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;

    @Override
    public ShoppingCartDto getByUser(User user) {
        Optional<ShoppingCart> optionalShoppingCart = shoppingCartRepository
                .findByUserId(user.getId());
        if (optionalShoppingCart.isPresent()) {
            return shoppingCartMapper.toDto(optionalShoppingCart.get());
        }
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        return shoppingCartMapper.toDto(shoppingCartRepository.save(shoppingCart));
    }
}
