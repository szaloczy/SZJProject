package com.szj.demo.service;
import com.szj.demo.dtos.ProductDTO;
import com.szj.demo.model.Product;
import com.szj.demo.model.ProductRequest;
import com.szj.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final JwtService jwtService;

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public ProductDTO createProduct(String jwtToken, ProductRequest myProduct) {
        return null;
    }
}
