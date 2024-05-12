package com.spark.scrapper;

import com.spark.feign.client.CryptoDataServiceClient;
import com.spark.feign.client.WebSocketServiceClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackageClasses = {
        WebSocketServiceClient.class,
        CryptoDataServiceClient.class
})
public class ScrapperApplication {
    public static void main(String[] args) {
        SpringApplication.run(ScrapperApplication.class, args);
    }
}
