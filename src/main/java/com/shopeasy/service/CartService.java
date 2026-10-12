package com.shopeasy.service;

import com.shopeasy.model.CartItem;
import com.shopeasy.model.Product;
import com.shopeasy.repository.CartItemRepository;
import com.shopeasy.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    public List<CartItem> getCartItems(Long userId) {
        return cartItemRepository.findByUserId(userId);
    }

    @Transactional
    public CartItem addToCart(Long userId, Long productId, int quantity) {
        Optional<CartItem> existing = cartItemRepository.findByUserIdAndProductId(userId, productId);
        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + Math.max(1, quantity));
            return cartItemRepository.save(item);
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));

        CartItem item = new CartItem(
                userId,
                product.getId(),
                product.getTitle(),
                product.getPrice(),
                product.getThumbnail(),
                Math.max(1, quantity)
        );
        return cartItemRepository.save(item);
    }

    @Transactional
    public CartItem updateQuantity(Long userId, Long productId, int quantity) {
        Optional<CartItem> existing = cartItemRepository.findByUserIdAndProductId(userId, productId);
        if (existing.isPresent()) {
            CartItem item = existing.get();
            if (quantity <= 0) {
                cartItemRepository.delete(item);
                return null;
            }
            item.setQuantity(quantity);
            return cartItemRepository.save(item);
        }
        return null;
    }

    @Transactional
    public void removeFromCart(Long userId, Long productId) {
        cartItemRepository.findByUserIdAndProductId(userId, productId)
                .ifPresent(cartItemRepository::delete);
    }

    @Transactional
    public void clearCart(Long userId) {
        cartItemRepository.deleteByUserId(userId);
    }
}
