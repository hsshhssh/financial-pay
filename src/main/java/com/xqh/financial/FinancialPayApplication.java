package com.xqh.financial;

import com.github.zkclient.ZkClient;
import com.xqh.financial.utils.DozerUtils;
import com.xqh.financial.utils.ruixun.CertificateUtils;
import org.dozer.DozerBeanMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@MapperScan(basePackages = "com.xqh.financial.mapper")
@EnableSwagger2
@EnableAsync
@EnableScheduling
public class FinancialPayApplication {


    public static void main(String[] args) {
        ConfigurableApplicationContext ac = SpringApplication.run(FinancialPayApplication.class, args);

        ac.getBean(CertificateUtils.class).initMap();
    }


    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.xqh.financial.controller.impl"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("支付平台接口文档")
                .contact("hssh")
                .version("1.0")
                .build();
    }


    @Bean
    public DozerBeanMapper dozerBean() {
        List<String> mappingFiles = Arrays.asList(
                "dozer/dozer-mapping.xml"
        );

        DozerBeanMapper dozerBean = new DozerBeanMapper();
        dozerBean.setMappingFiles(mappingFiles);
        return dozerBean;
    }

    @Bean
    public DozerUtils dozerUtils() {
        return new DozerUtils();
    }

    @Bean
    public ZkClient zkClient()
    {
        return new ZkClient(System.getenv("ZK_HOST"));
    }

}
