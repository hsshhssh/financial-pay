package com.xqh.financial;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.xqh.financial.mapper")
public class FinancialPayApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinancialPayApplication.class, args);
	}
}
