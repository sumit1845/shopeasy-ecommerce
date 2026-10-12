package com.shopeasy.service;

import com.shopeasy.model.Product;
import com.shopeasy.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getProducts(String category, String search, String sort) {
        List<Product> result;

        if (search != null && !search.trim().isEmpty()) {
            result = productRepository.searchProducts(search.trim());
        } else if (category != null && !category.trim().equalsIgnoreCase("all") && !category.trim().isEmpty()) {
            result = productRepository.findByCategoryIgnoreCase(category.trim());
        } else {
            result = productRepository.findAll();
        }

        // Apply sorting
        if (sort != null) {
            switch (sort.trim().toLowerCase()) {
                case "low" -> result.sort(Comparator.comparingDouble(p -> p.getPrice() != null ? p.getPrice() : 0.0));
                case "high" -> result.sort((a, b) -> Double.compare(
                        b.getPrice() != null ? b.getPrice() : 0.0,
                        a.getPrice() != null ? a.getPrice() : 0.0));
                case "rating" -> result.sort((a, b) -> Double.compare(
                        b.getRating() != null ? b.getRating() : 0.0,
                        a.getRating() != null ? a.getRating() : 0.0));
                case "name" -> result.sort((a, b) -> {
                    String titleA = a.getTitle() != null ? a.getTitle() : "";
                    String titleB = b.getTitle() != null ? b.getTitle() : "";
                    return titleA.compareToIgnoreCase(titleB);
                });
                default -> {}
            }
        }

        return result;
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public List<String> getCategories() {
        return productRepository.findDistinctCategories();
    }

    public List<Product> getRandomProducts(int limit) {
        List<Product> all = new ArrayList<>(productRepository.findAll());
        Collections.shuffle(all);
        return all.stream().limit(Math.max(1, limit)).collect(Collectors.toList());
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public List<Product> saveAll(List<Product> products) {
        return productRepository.saveAll(products);
    }

    public long count() {
        return productRepository.count();
    }
}
