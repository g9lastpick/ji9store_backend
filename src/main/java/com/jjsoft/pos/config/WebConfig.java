package com.jjsoft.pos.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.jjsoft.pos.security.StoreAccessInterceptor;

import lombok.RequiredArgsConstructor;
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer{

	@Value("${file.upload.image-path}")
    private String imagePath;

	private final StoreAccessInterceptor storeAccessInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 점포 스코프 관리자 API 의 storeId 파라미터 검증 (다른 점포 접근 차단)
		// (/api/common/** 은 공개 API 이므로 제외)
		registry.addInterceptor(storeAccessInterceptor)
				.addPathPatterns("/api/admin/**");
	}


	@Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173", "https://jjpos.store", "https://g9system.com", "https://dev.g9system.com", "https://www.g9system.com", "https://www.jjpos.store")  // Vue 개발 서버
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true);  // 쿠키 필요하면 추가
    }
	 
	 
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/images/products/**")
                .addResourceLocations("file:" + imagePath);
    }
}
