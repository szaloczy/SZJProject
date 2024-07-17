package com.szj.demo.service;

import com.szj.demo.dtos.product.CartItemDTO;
import com.szj.demo.model.Cart;
import com.szj.demo.model.CartItem;
import com.szj.demo.model.Product;
import com.szj.demo.model.User;
import com.szj.demo.repository.CartItemRepository;
import com.szj.demo.repository.CartRepository;
import com.szj.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    public Cart getCartByUserId(Long userId) {
        return cartRepository.findCartByUserId(userId).orElseGet(() -> {
            Optional<User> optUser = userRepository.findById(userId);

            if (optUser.isEmpty()) {
                throw new IllegalArgumentException("User does not exists in repository!");
            }
            User user = optUser.get();
            Cart cart = new Cart();
            cart.setUser(user);
            return cartRepository.save(cart);
        });
    }

    public CartItemDTO addItemToCart(User user, Product product, int quantity) {
        Cart cart = cartRepository.findCartByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getCartProduct().equals(product))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setCartItemQuantity(item.getCartItemQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setCartProduct(product);
            newItem.setCartItemQuantity(quantity);
            cart.getCartItems().add(newItem);
        }
        Cart savedCart = cartRepository.save(cart);
        return new CartItemDTO(savedCart);
    }

    public Cart removeItemFromCart(User user, Product product, int quantityToRemove) {
        Cart cart = getCartByUserId(user.getId());

        List<CartItem> itemsToRemove = cart.getCartItems().stream()
                .filter(item -> item.getCartProduct().equals(product))
                .toList();

        int removedCount = 0;
        for (CartItem item : itemsToRemove) {
            if (removedCount >= quantityToRemove) {
                break;
            }
            if (item.getCartItemQuantity() >= quantityToRemove) {
                item.setCartItemQuantity(item.getCartItemQuantity() - quantityToRemove);
                removedCount += quantityToRemove;

                if (item.getCartItemQuantity() == 0) {
                    cart.getCartItems().remove(item);
                    cartItemRepository.delete(item);
                }
            } else {
                removedCount += item.getCartItemQuantity();
                cart.getCartItems().remove(item);
                cartItemRepository.delete(item);
            }
        }

            cart.getCartItems().removeIf(item -> item.getCartItemQuantity() == 0);
            return cartRepository.save(cart);

    }

        public void clearCart (User user){
            Cart cart = getCartByUserId(user.getId());
            cart.getCartItems().clear();
            cartRepository.save(cart);
        }

    }

