package com.djhhh.securityrbacdemo.security;

import com.djhhh.securityrbacdemo.result.Result;
import com.djhhh.securityrbacdemo.utils.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

/**
 * Spring Security 安全配置类
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(JwtTokenUtil jwtTokenUtil, UserDetailsService userDetailsService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 密码编码器 Bean
     * 使用 BCrypt 强哈希算法加密密码
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * JWT链引入
     * @return
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenUtil, userDetailsService);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * 安全过滤器链配置（核心安全规则）
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                //--- 请求授权规则 ---
                .authorizeHttpRequests(auth -> auth
                        // 白名单路径（无需认证）
                        .requestMatchers(
                                "/index.html",
                                "/user/logout",   //注册API
                                "/user/login",   //注册API
                                "/user/register",   //注册API
                                "/doc.html",                // Swagger 文档页
                                "/webjars/**",              // Swagger WebJars 资源
                                "/v3/api-docs/**",         // OpenAPI 文档端点
                                "/swagger-ui/**",           // Swagger UI 资源
                                "/swagger-resources/**",    // Swagger 资源配置
                                "/favicon.ico",             // 网站图标
                                "/login.html",              // 自定义登录页（根据实际路径调整）
                                "/css/**",                  // CSS 静态资源
                                "/js/**"                    // JavaScript 静态资源
                        ).permitAll()
                        // 其他所有请求需要认证
                        .anyRequest().authenticated()
                )

                //--- 表单登录配置 ---
//                .formLogin(form -> form
//                        .defaultSuccessUrl("/index.html")
//                        .loginProcessingUrl("/user/login")    // 表单提交地址
//                        // 登录成功处理（可跳转页面或返回 JSON）
//                        .successHandler((request, response, authentication) -> {
//                            // 示例：返回 JSON 响应（适合前后端分离）
//                            response.setContentType("application/json;charset=UTF-8");
//                            response.getWriter().write(
//                                    objectMapper.writeValueAsString(Result.ok("登录成功"))
//                            );
//                        })
//                        // 登录失败处理
//                        .failureHandler((request, response, exception) -> {
//                            String errorMessage;
//                            if (exception instanceof BadCredentialsException) {
//                                errorMessage = "密码错误";
//                            } else if (exception instanceof UsernameNotFoundException) {
//                                errorMessage = "用户不存在";
//                            } else {
//                                errorMessage = "登录失败";
//                            }
//                            response.setContentType("application/json;charset=UTF-8");
//                            response.setStatus(401);
//                            response.getWriter().write(
//                                    objectMapper.writeValueAsString(Result.fail(401, errorMessage))
//                            );
//                        })
//                        .permitAll()  // 允许所有人访问登录页
//                )

                //--- 注销配置 ---
//                .logout(logout -> logout
//                        .logoutUrl("/user/logout")           // 注销请求地址
//                )

                //--- 会话管理 ---
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 无状态
                )

                //--- 添加JWT校验 ---
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)

                //--- 异常处理 ---
                .exceptionHandling(exceptions -> exceptions
                        // 未认证处理（访问需要登录的资源）
                        .authenticationEntryPoint((request, response, ex) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            response.setStatus(401);
                            response.getWriter().write(
                                    objectMapper.writeValueAsString(Result.fail(401, "请先登录"))
                            );
                        })
                        // 权限不足处理
                        .accessDeniedHandler((request, response, ex) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            response.setStatus(403);
                            response.getWriter().write(
                                    objectMapper.writeValueAsString(Result.fail(403, "权限不足"))
                            );
                        })
                )

                //--- CORS 跨域配置（按需启用）---
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                //--- CSRF 防护（传统 Web 应用建议启用）---
                .csrf(AbstractHttpConfigurer::disable); // 禁用 CSRF

        return http.build();
    }

    /**
     * CORS 跨域配置（生产环境应缩小范围）
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:8080")); // 允许的前端地址
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        config.setAllowCredentials(true); // 允许携带 Cookie
        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}