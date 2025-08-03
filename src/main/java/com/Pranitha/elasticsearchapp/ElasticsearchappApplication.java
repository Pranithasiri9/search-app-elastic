package com.Pranitha.elasticsearchapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;




@SpringBootApplication
@EnableElasticsearchRepositories(basePackages = "com.pranitha.elasticsearchapp.repository")
public class ElasticsearchappApplication {

	public static void main(String[] args) {
		SpringApplication.run(ElasticsearchappApplication.class, args);
	}

}
