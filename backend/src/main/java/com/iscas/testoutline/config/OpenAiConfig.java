package com.iscas.testoutline.config;

import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;

@Configuration
public class OpenAiConfig {

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.proxy-host:#{null}}")
    private String proxyHost;

    @Value("${openai.proxy-port:#{null}}")
    private Integer proxyPort;

    @Bean
    public OpenAiService openAiService() {
        if (proxyHost != null && proxyPort != null) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
            return new OpenAiService(apiKey);
        }
        return new OpenAiService(apiKey);
    }
} 