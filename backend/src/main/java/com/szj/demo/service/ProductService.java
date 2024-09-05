package com.szj.demo.service;
import com.szj.common.fileServer.model.FileEntity;
import com.szj.common.fileServer.services.FileServerService;
import com.szj.demo.dtos.product.ProductDTO;
import com.szj.demo.model.Product;
import com.szj.demo.model.ProductRequest;
import com.szj.demo.model.User;
import com.szj.demo.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final FileServerService fileServerService;

    public ProductDTO createProduct(User user, ProductRequest productRequest, MultipartFile file) {
        try {
            validateProductRequest(productRequest);

            FileEntity fileEntity = null;
            if(file != null && !file.isEmpty()) {
                fileEntity = fileServerService.upload(file);
                fileEntity.inUse = true;
            }

            UUID fileEntityId = fileEntity != null ? fileEntity.getId() : null;

            Product product = new Product(
                    user.getUsername(),
                    productRequest.getProductName(),
                    productRequest.getProductDescription(),
                    productRequest.getProductCondition(),
                    productRequest.getPrice(),
                    productRequest.getStock(),
                    fileEntityId
            );

            Product savedProduct = productRepository.save(product);
            return new ProductDTO(savedProduct);

        } catch (IllegalStateException | IOException e) {
            throw new IllegalArgumentException("Auction creation failed: " + e.getMessage());
        }
    }

    private void validateProductRequest(ProductRequest productRequest) {
        if (productRequest == null) {
            throw new IllegalStateException("Product request cannot be null!");
        }
        if (productRequest.getProductName() == null || productRequest.getProductName().isEmpty()) {
            throw new IllegalStateException("Product name cannot be null or empty!");
        }
        if (productRequest.getProductName().length() < 3 || productRequest.getProductName().length() > 50) {
            throw new IllegalStateException("Product name must be between 3 and 50 characters!");
        }
        if (productRequest.getProductCondition() == null) {
            throw new IllegalStateException("Product condition cannot be null!");
        }
        if (productRequest.getProductDescription() == null || productRequest.getProductDescription().isEmpty()) {
            throw new IllegalStateException("Product description cannot be null or empty!");
        }
        if (productRequest.getProductDescription().length() < 3) {
            throw new IllegalStateException("Product description must be at least 3 characters long!");
        }
        if (productRequest.getPrice() < 1) {
            throw new IllegalStateException("Product price must be greater than or equal to 1!");
        }
    }

    public List<Product> getAll(){
        return productRepository.findAll();
    }

    public Optional<Product> findProductByProductId(Long id) {
        return productRepository.findProductByProductId(id);
    }

    public Product updateProductByProductId(Product product){
        return productRepository.save(product);
    }

    @Transactional
    public void delete(Product product, User user){
        if(user.getUsername().equals(product.getSeller())) {
            productRepository.deleteProductByProductId(product.getProductId());
        } else {
            throw new IllegalArgumentException("You do not own this product");
        }
    }
}
