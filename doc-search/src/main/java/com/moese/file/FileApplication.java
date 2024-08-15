package com.moese.file;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
//@EnableElasticsearchRepositories(basePackages = "com.moese.file.repository")
@EnableTransactionManagement
@MapperScan("com.moese.file.mapper")
@Configuration
public class FileApplication {




	public static void main(String[] args) {
		SpringApplication.run(FileApplication.class, args);
	}
}
