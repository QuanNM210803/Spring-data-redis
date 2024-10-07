package com.example.springdataredis.repository;

import com.example.springdataredis.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ProductDao {
    public static final String HASH_KEY="Product-";

    @Autowired
    private RedisTemplate<String, Object> template;

    public Product save(Product product){
        template.opsForHash().put(HASH_KEY+String.valueOf(product.getId()),HASH_KEY,product);
        return product;
    }

    public List<Product> findAll(){
        Set<String> keys = template.keys("Product-*");

        return Objects.requireNonNull(keys).stream()
                .map(key -> template.opsForHash().get(key, HASH_KEY))
                .filter(Objects::nonNull)
                .map(product -> (Product) product)
                .collect(Collectors.toList());
    }

    public Product findProductById(int id){
        System.out.println("Caching: " + id);
        return (Product) template.opsForHash().get(HASH_KEY+String.valueOf(id), HASH_KEY);

    }


    public String deleteProduct(int id){
        template.opsForHash().delete(HASH_KEY+String.valueOf(id), HASH_KEY);
        return "product removed !!";
    }
}

