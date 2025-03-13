package com.iscas.testoutline.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("测试大纲智能生成工具 API文档")
                        .version("1.0")
                        .description("基于大语言模型的测试大纲智能生成工具API接口文档")
                        .contact(new Contact()
                                .name("ISCAS")
                                .email("support@iscas.ac.cn")
                                .url("https://www.iscas.ac.cn")));
    }
}