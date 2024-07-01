package com.szj.demo.controller;

import com.szj.demo.annotations.RequiredAuthenticationLevel;
import com.szj.demo.dtos.ProductDTO;
import com.szj.demo.dtos.ProductUpdateDTO;
import com.szj.demo.enums.AuthenticationLevel;
import com.szj.demo.exception.InvalidTokenException;
import com.szj.demo.model.ApiResponse;
import com.szj.demo.model.Product;
import com.szj.demo.model.ProductRequest;
import com.szj.demo.model.User;
import com.szj.demo.service.ProductService;
import com.szj.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;

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
    public ResponseEntity<ApiResponse<ProductDTO>> createProduct(@RequestBody ProductRequest myProduct){

        try{
            ProductDTO productDTO = productService.createProduct(userService.currentUser(), myProduct);
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
                throw new IllegalAccessException("User does not have access to modify");
            }

            productToBeModified.get().update(modification);
            Product updatedProduct = productService.updateProductByProductId(productToBeModified.get());

            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, new ProductDTO(updatedProduct), ""));
        } catch(IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null,e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false,null,e.getMessage()));
        }
    }

}
