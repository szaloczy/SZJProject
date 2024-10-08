package com.szj.demo.controller;

import com.szj.demo.annotations.RequiredAuthenticationLevel;
import com.szj.demo.dtos.product.ProductDTO;
import com.szj.demo.dtos.product.ProductUpdateDTO;
import com.szj.demo.enums.AuthenticationLevel;
import com.szj.demo.exception.InvalidTokenException;
import com.szj.demo.model.ApiResponse;
import com.szj.demo.model.Product;
import com.szj.demo.model.ProductRequest;
import com.szj.demo.model.User;
import com.szj.demo.service.ProductService;
import com.szj.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/products")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final UserService userService;

    @RequiredAuthenticationLevel(level = AuthenticationLevel.PRIVATE)
    @GetMapping()
    public List<Product> getAll(){
        return productService.getAll();
    }

    @RequiredAuthenticationLevel(level = AuthenticationLevel.PRIVATE)
    @PostMapping()
    public ResponseEntity<ApiResponse<ProductDTO>> createProduct(@RequestParam(value = "file") MultipartFile file, ProductRequest myProduct){

        try{
            ProductDTO productDTO = productService.createProduct(userService.currentUser(), myProduct, file);
            return ResponseEntity.ok().body(new ApiResponse<>(true, productDTO,""));
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(false, null, "Invalid token!"));
        }
        catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, null, "Product creation failed!"));
        } catch (HttpServerErrorException.InternalServerError e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Endpoint not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, null, e.getMessage()));
        }
    }


    @RequiredAuthenticationLevel(level = AuthenticationLevel.PRIVATE)
    @PutMapping
    public ResponseEntity<ApiResponse<ProductDTO>> modify(@RequestBody ProductUpdateDTO modification){
        try {
            Optional<Product> productToBeModified = productService.findProductByProductId(modification.getProductId());
            if(productToBeModified.isEmpty()){
                throw new IllegalArgumentException("Auction does not exist!");
            }

            User user = userService.currentUser();

            if(!productToBeModified.get().getSeller().equals(user.getUsername())) {
                throw new AccessDeniedException("User does not have access to modify");
            }

            productToBeModified.get().update(modification);
            Product updatedProduct = productService.updateProductByProductId(productToBeModified.get());

            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, new ProductDTO(updatedProduct), ""));
        } catch(IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null,e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, null, e.getMessage()));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false,null,e.getMessage()));
        }
    }

    @RequiredAuthenticationLevel(level = AuthenticationLevel.PRIVATE)
    @DeleteMapping
    public ResponseEntity<ApiResponse<Long>> deleteProduct(@RequestParam Long productId) {
        try{
            Optional<Product> productToBeDeleted = productService.findProductByProductId(productId);
            if(productToBeDeleted.isEmpty()) throw new IllegalArgumentException("Product does not exists in repository");
            Product product = productToBeDeleted.get();

            productService.delete(product, userService.currentUser());
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true,null,""));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false,null,e.getMessage()));
        }
    }

}
