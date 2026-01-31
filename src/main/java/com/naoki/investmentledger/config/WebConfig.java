package com.naoki.investmentledger.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // すべてのパス (/**) に対するリクエストで
        registry.addMapping("/**")
                // ReactのURL (http://localhost:5173) からのアクセスを許可する
                .allowedOrigins("http://localhost:5173")
                // 許可するHTTPメソッド
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
