package com.technokratos.config;

import com.mongodb.client.MongoClient;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.mongodb.sync.v4.driver.MongoSync4Driver;
import io.mongock.runner.springboot.EnableMongock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableMongock
public class MongockConfig {

    @Bean
    public ConnectionDriver mongockDriver(MongoClient mongoClient) {
        return MongoSync4Driver.withDefaultLock(mongoClient,"location");
    }


}

