package com.hai.controller;

import com.hai.exception.ProductNotFoundException;
import com.hai.mapper.ProductMapper;
import com.hai.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

/**
 * Created by Administrator on 2017/11/13.
 */
@RestController
@RequestMapping("/product")
public class ProductController {

    private Logger logger = Logger.getLogger(ProductController.class.getName());

    @Autowired
    private ProductMapper productMapper;

    @GetMapping
    public String product() {
        return "hello welcome to product page!";
    }

    @GetMapping("/{id}")
    public Product getProductInfo(@PathVariable("id") int productId) {
        System.out.println(this.getClass().getName() + ".getProductInfo...");
        return productMapper.select(productId);
    }


    @PutMapping("/{id}")
    public Product updateProductInfo(@PathVariable("id") int id, @RequestBody Product product) {
        System.out.println(this.getClass().getName() + ".updateProductInfo...");
        Product p = productMapper.select(id);
        if (null == p) {
            throw new ProductNotFoundException(id);
        }
        p.setName(product.getName());
        p.setPrice(product.getPrice());
        productMapper.update(p);
        return p;
    }

    @PostMapping("/{id}")
    public Product updateProductInfoByPost(@PathVariable("id") int id, @RequestBody Product product) {
        System.out.println(this.getClass().getName() + ".updateProductInfoByPost...");
        Product p = productMapper.select(id);
        if (null == p) {
            throw new ProductNotFoundException(id);
        }
        p.setName(product.getName());
        p.setPrice(product.getPrice());
        productMapper.update(p);
        return p;
    }

}
