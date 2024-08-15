package com.szj.demo.service;

import com.szj.demo.dtos.cart.CartItemDTO;
import com.szj.demo.exception.InvalidOrderException;
import com.szj.demo.exception.InvalidProductException;
import com.szj.demo.model.Cart;
import com.szj.demo.model.CartItem;
import com.szj.demo.model.Product;
import com.szj.demo.model.User;
import com.szj.demo.repository.CartItemRepository;
import com.szj.demo.repository.CartRepository;
import com.szj.demo.repository.ProductRepository;
import com.szj.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public Cart getCartByUserId(User user) {
        return cartRepository.findCartByUserId(user.getId()).orElseGet(() -> {
            Optional<User> optUser = userRepository.findById(user.getId());

            if (optUser.isEmpty()) {
                throw new IllegalArgumentException("User does not exists in repository!");
            }
            User existingUser = optUser.get();
            Cart cart = new Cart();
            cart.setUser(existingUser);
            return cartRepository.save(cart);
        });
    }

    public CartItemDTO addItemToCart(User user, Long productId, int quantity) throws InvalidProductException {
            Cart cart = cartRepository.findCartByUser(user)
                    .orElseGet(() -> {
                        Cart newCart = new Cart();
                        newCart.setUser(user);
                        return cartRepository.save(newCart);
                    });

            Product product = findProductById(productId);

            Optional<CartItem> existingItem = cart.getCartItems().stream()
                    .filter(item -> item.getCartProduct().equals(product))
                    .findFirst();

            if (existingItem.isPresent() && isItemAvailable(product, quantity)) {
                CartItem item = existingItem.get();
                item.setCartItemQuantity(item.getCartItemQuantity() + quantity);
                cartItemRepository.save(item);
            } else if(isItemAvailable(product, quantity)) {
                CartItem newItem = new CartItem();
                newItem.setCart(cart);
                newItem.setCartProduct(product);
                newItem.setCartItemQuantity(quantity);
                cart.getCartItems().add(newItem);
            }
            Cart savedCart = cartRepository.save(cart);
            return new CartItemDTO(savedCart);
    }

    private Product findProductById(long productId){
        Optional<Product> optProduct =  productRepository.findProductByProductId(productId);
        if(optProduct.isEmpty()){
            throw new IllegalArgumentException("Product does not exists in repository!");
        }
       return optProduct.get();
    }

    private boolean isItemAvailable(Product product , int quantity) throws InvalidProductException {
        Optional<Product> optProduct = productRepository.findProductByProductId(product.getProductId());
        if (optProduct.isEmpty()) {
            throw new InvalidProductException("Product does not exists in repository!");
        }
        Product existingProduct = optProduct.get();
        if(existingProduct.getStock() >= quantity) {
            return true;
        } else {
            throw new InvalidProductException("Product has not enough stock!");
        }
    }

    public void removeItemFromCart(User user, Product product, int quantityToRemove) {
        Cart cart = getCartByUserId(user);

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
            cartRepository.save(cart);

    }

    @Transactional
        public void clearCart (User user){
         Optional<Cart> optCart = cartRepository.findCartByUser(user);
         if(optCart.isEmpty()) {
             throw new IllegalArgumentException("Cart does not exists in repository!");
         }

            Cart cart = optCart.get();
         cartItemRepository.deleteCartItemsByCart_CartId(cart.getCartId());
         cart.getCartItems().clear();
         cartRepository.save(cart);
        }

    }

