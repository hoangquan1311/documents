package com.javatechie.redis.controller;

import com.javatechie.redis.entity.Product;
import com.javatechie.redis.service.ProductSerivce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/x`")
public class ProductController {
    @Autowired
    private ProductSerivce productSerivce;

    @PostMapping()
    public Product save(@RequestBody Product product) {
        return productSerivce.save(product);
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productSerivce.findAll();
    }

    @GetMapping("/{id}")
    public Product findProduct(@PathVariable int id) {
        return productSerivce.findProductById(id);
    }
    @DeleteMapping("/{id}")
    public String remove(@PathVariable int id)   {
        return productSerivce.deleteProduct(id);
    }
}
