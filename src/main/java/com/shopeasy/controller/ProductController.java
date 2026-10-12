package com.shopeasy.controller;

import com.shopeasy.model.Product;
import com.shopeasy.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sort) {

        List<Product> products = productService.getProducts(category, search, sort);
        Map<String, Object> response = new HashMap<>();
        response.put("products", products);
        response.put("total", products.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(productService.getCategories());
    }

    @GetMapping("/random")
    public ResponseEntity<List<Product>> getRandomProducts(@RequestParam(defaultValue = "12") int limit) {
        return ResponseEntity.ok(productService.getRandomProducts(limit));
    }
}
