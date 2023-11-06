package com.a1dnan.billingservice;

import com.a1dnan.billingservice.entities.Bill;
import com.a1dnan.billingservice.entities.ProductItem;
import com.a1dnan.billingservice.model.Customer;
import com.a1dnan.billingservice.model.Product;
import com.a1dnan.billingservice.repository.BillRepository;
import com.a1dnan.billingservice.repository.ProductItemRepository;
import com.a1dnan.billingservice.services.CustomerRestClient;
import com.a1dnan.billingservice.services.ProductRestClient;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

@SpringBootApplication
@EnableFeignClients
public class BillingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BillingServiceApplication.class, args);
    }


    @Bean
    CommandLineRunner start(BillRepository billRepository,
                            ProductItemRepository productItemRepository,
                            CustomerRestClient customerRestClient,
                            ProductRestClient productRestClient){
        return args -> {
            Collection<Product> products = productRestClient.allProducts().getContent();
            Long customerId = 1L;
            Customer customer = customerRestClient.findCustomerById(customerId);
            if (customer == null) throw new EntityNotFoundException("Customer not found");
            Bill bill = new Bill();
            bill.setBillDate(new Date());
            bill.setCustomerId(customerId);
            Bill savedBill = billRepository.save(bill);
            products.forEach(product -> {
                ProductItem productItem = new ProductItem();
                productItem.setBill(savedBill);
                productItem.setProductId(product.getId());
                productItem.setPrice(product.getPrice());
                productItem.setQuantity(1+new Random().nextInt(10));
                productItem.setDiscount(Math.random());
                productItemRepository.save(productItem);
            });
        };
    }
}
