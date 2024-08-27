package com.example.demo.Service;

import com.example.demo.Entity.Product;
import com.example.demo.Entity.ProductDocument;
import com.example.demo.Repository.ProductDocumentRepository;
import com.example.demo.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductDocumentRepository productDocumentRepository;

    @Transactional
    public Product createProduct(Product product) {
        Product savedProduct = productRepository.save(product);

        ProductDocument productDocument = new ProductDocument();
        productDocument.setId(savedProduct.getId());
        productDocument.setName(savedProduct.getName());
        productDocument.setDescription(savedProduct.getDescription());
        productDocument.setPrice(savedProduct.getPrice());

        productDocumentRepository.save(productDocument);

        return savedProduct;
    }
    public List<Product> findAll() {
        return productRepository.findAll();
    }
    @Transactional
    public Product updateProduct(Long id, Product updatedProduct) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        Product savedProduct = productRepository.save(existingProduct);

        ProductDocument productDocument = new ProductDocument();
        productDocument.setId(savedProduct.getId());
        productDocument.setName(savedProduct.getName());
        productDocument.setDescription(savedProduct.getDescription());
        productDocument.setPrice(savedProduct.getPrice());

        productDocumentRepository.save(productDocument);

        return savedProduct;
    }
    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
        productDocumentRepository.deleteById(id);
    }
    @Scheduled(fixedRate = 60000)
    public void syncProducts() {
        List<Product> products = productRepository.findAll();

        List<ProductDocument> productDocuments = products.stream().map(product -> {
            ProductDocument document = new ProductDocument();
            document.setId(product.getId());
            document.setName(product.getName());
            document.setDescription(product.getDescription());
            document.setPrice(product.getPrice());
            return document;
        }).collect(Collectors.toList());

        productDocumentRepository.saveAll(productDocuments);
    }
}
