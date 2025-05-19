package vn.iotstar.configs;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import vn.iotstar.filters.JwtTokenFilter;

@Configuration
@EnableWebSecurity
@EnableWebMvc
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> {
                    requests
                            .requestMatchers(
                                    "users/**",
                                    "admin/home2",
                                    "users/forgot-password",
                                    "users/reset-password",
                                    "users/**",
                                    "users/verify-otp",
                                    "categories",
                                    "category/**",
                                    "address/**",
                                    "cart/**",
                                    "contact/**",
                                    "like/**",
                                    "order/**",
                                    "/**",
                                    "voucher/**",
                                    "templates/**",
                                    "images/**",
                                    "shipper/**",
                                    "profile",
                                    "vendor/**"
                                    
                            )
                            .permitAll()
                            .requestMatchers("/css/dep.css", "/js/script.js", "/css/style.css", "/images/qr.png", "/fonts/**", "/**/*.css", "/**/*.js",
                            		"/**/*.jpg", "/**/*.png", "/css/style1.css", "/css/AdminLTE.css","/css/_all-skins.min.css", "/css/bootstrap.min1.css", 
                            		"/css/font-awesome.min.css","/css/jquery-ui.css","/css/nestable.css", "/assets/images/**",
                            		"/images/user2-160x160.jpg","/js/jquery.min.js","/js/jquery-ui.js","/js/bootstrap.min.js",
                            		"/js/adminlte.min.js","/js/dashboard.js","/js/function.js"
                            		,"/fonts/fontawesome-webfont.ttf","/fonts/fontawesome-webfont.woff","/fonts/fontawesome-webfont.woff2",
                            		"/images/**","images/user2-160x160.jpg"
                            )
                            .permitAll()
                            .anyRequest().authenticated();
                })
                .logout(logout -> logout
                        .logoutUrl("/users/logout") // Đường dẫn logout
                        .logoutSuccessUrl("/users/login") // Đường dẫn sau khi logout thành công
                        .invalidateHttpSession(true) // Xóa session khi logout
                        .deleteCookies("JSESSIONID") // Xóa cookie session
                );

        return http.build();
    }
}

