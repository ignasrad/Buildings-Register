package com.ignas.propertiesregister;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.Collections;
import java.util.stream.Collectors;


@Configuration
@EnableSwagger2WebMvc
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.ignas.propertiesregister"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(getInfo());
    }

    private ApiInfo getInfo(){
        return new ApiInfo(
                "Buildings register API",
                "Simple building register api",
                "1.0",
                "",
                new springfox.documentation.service.Contact("Ignas Radevicius", "spring.io", "sc18ir@leeds.ac.uk"),
                "",
                "Spring.io",
                Collections.emptyList());
    }
}