package vn.iotstar.configs;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
	    // Thêm cấu hình để Spring Boot phục vụ tệp từ thư mục ngoài
	    registry.addResourceHandler("/images/**")
	            .addResourceLocations("file:/C:/WebCuoiKi/images/") // Đảm bảo đường dẫn khớp với thư mục lưu ảnh ( final)
	    		.addResourceLocations("file:/C:/WebCuoiKi/reviews/");
	}
}