package com.szj.demo.repository;

import com.szj.demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Interface for managing auctions in a repository.
 */

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findProductByProductId(Long id);
    Optional<List<Product>> findProductsByProductId(Long id);
    void deleteProductByProductId(Long id);

}
