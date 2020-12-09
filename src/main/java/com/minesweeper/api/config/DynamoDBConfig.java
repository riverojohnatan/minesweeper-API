package com.minesweeper.api.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.minesweeper.api.repository.MineSweeperRepository;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDynamoDBRepositories(basePackageClasses = MineSweeperRepository.class)
public class DynamoDBConfig {

    @Value("${amazon.aws.secretkey}")
    private String secretKey;

    @Value("${amazon.aws.accesskey}")
    private String accessKey;

    @Bean
    public AmazonDynamoDB amazonDynamoDB(AWSCredentials awsCredentials) {
        @SuppressWarnings("deprecation")
        AmazonDynamoDB amazonDynamoDB = new AmazonDynamoDBClient(awsCredentials);
        return amazonDynamoDB;
    }

    @Bean
    public AWSCredentials awsCredentials() {
        return new BasicAWSCredentials(accessKey, secretKey);
    }
}
