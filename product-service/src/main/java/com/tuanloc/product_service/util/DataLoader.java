package com.tuanloc.product_service.util;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.tuanloc.product_service.model.Product;
import com.tuanloc.product_service.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner{
    private final ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception{
        if(productRepository.count() < 1){
            Product product = new Product();
            product.setName("iPhone 16");
            product.setDescription("iPhone 16");
            product.setPrice(BigDecimal.valueOf(1000));

            productRepository.save(product);
        }
    }
}
