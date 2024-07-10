package com.szj.demo.service;
import com.szj.demo.dtos.product.ProductDTO;
import com.szj.demo.model.Product;
import com.szj.demo.model.ProductRequest;
import com.szj.demo.model.User;
import com.szj.demo.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductDTO createProduct(User user, ProductRequest productRequest) {

        validateProductRequest(productRequest);

        Product product = new Product(
                user.getUsername(),
                productRequest.getProductName(),
                productRequest.getProductDescription(),
                productRequest.getProductCondition(),
                productRequest.getPrice(),
                productRequest.getStock()
        );
       Product savedProduct = productRepository.save(product);

       return new ProductDTO(savedProduct);
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
