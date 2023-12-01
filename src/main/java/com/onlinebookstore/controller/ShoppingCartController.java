package com.onlinebookstore.controller;

import com.onlinebookstore.dto.cartitem.CartItemDto;
import com.onlinebookstore.dto.cartitem.CartItemRequestDto;
import com.onlinebookstore.dto.cartitem.CartItemUpdateRequestDto;
import com.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import com.onlinebookstore.model.User;
import com.onlinebookstore.service.cartitem.CartItemService;
import com.onlinebookstore.service.shoppingcart.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping cart management", description = "Endpoints for managing shopping carts")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;
    private final CartItemService cartItemService;

    @GetMapping
    @Operation(summary = "Get shopping cart",
            description = "Get the user's shopping cart with cart items")
    public ShoppingCartDto getShoppingCart(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.getByUser(user);
    }

    @PostMapping
    @Operation(summary = "Add cart item",
            description = "Add cart item into the user's shopping cart")
    public CartItemDto saveCartItem(Authentication authentication,
                                    @RequestBody @Valid CartItemRequestDto requestDto) {
        User user = (User) authentication.getPrincipal();
        return cartItemService.save(user.getId(), requestDto);
    }

    @PutMapping("/cart-items/{cartItemId}")
    @Operation(summary = "Update cart item",
            description = "Update cart item in the user's shopping cart")
    public void updateCartItem(@PathVariable Long cartItemId,
                               @RequestBody @Valid CartItemUpdateRequestDto requestDto) {
        cartItemService.update(cartItemId, requestDto);
    }

    @DeleteMapping("/cart-items/{cartItemId}")
    @Operation(summary = "Delete cart item",
            description = "Delete an existing cart item by id in the user's shopping cart")
    public void deleteCartItem(@PathVariable Long cartItemId) {
        cartItemService.deleteById(cartItemId);
    }
}
