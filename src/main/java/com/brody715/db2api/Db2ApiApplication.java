package com.brody715.db2api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;

@Slf4j
@SpringBootApplication
@ServletComponentScan
@EnableAutoConfiguration
@ConfigurationPropertiesScan
public class Db2ApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(Db2ApiApplication.class, args);
		log.info("Start successfully");
	}

}
