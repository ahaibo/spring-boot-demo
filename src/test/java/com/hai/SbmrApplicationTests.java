package com.hai;

import com.hai.model.Product;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
/*
对应于application.yml文件中的
    spring:
        profiles: test
*/
@ActiveProfiles(profiles = "test")
public class SbmrApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void contextLoads() {
        System.out.println(this.getClass().getName() + ".contextLoads...");
    }


    @Test
    public void test() {
        int productId = 1;
        String url = "http://localhost:" + port + "/product/" + productId;
        Product product = testRestTemplate.getForObject(url, Product.class);

        assertThat(product.getPrice()).isEqualTo(200F);

        Product newProduct = new Product();
        int newPrice = new Random().nextInt();
        newProduct.setName("new name");
        newProduct.setPrice(newPrice);
        testRestTemplate.put(url, newProduct);

        Product testProduct = testRestTemplate.getForObject(url, Product.class);
        assertThat(testProduct.getPrice()).isEqualTo(newPrice);

    }

}
