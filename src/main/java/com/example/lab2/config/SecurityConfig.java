package com.example.lab2.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.lab2.entity.User.UserRole;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/api/booking/create").hasAuthority(UserRole.USER.name())
                                .requestMatchers("/api/booking/**").authenticated()

                                .requestMatchers("/api/post/create").hasAuthority(UserRole.USER.name())
                                .requestMatchers("/api/post").permitAll()
                                .requestMatchers("/api/post/posts/page").permitAll()
                                .requestMatchers("/api/post/photo").permitAll()
                                .requestMatchers("/api/post/**").authenticated()

                                .requestMatchers("/api/user/sign-up").anonymous()
                                .requestMatchers("/api/user/score").permitAll()
                                .requestMatchers("/api/user/**").authenticated()

                                .requestMatchers("/api/booking/**").authenticated()

                                .requestMatchers("/api/review/create").hasAuthority(UserRole.USER.name())
                                .requestMatchers("/api/review/booking").authenticated()
                                .requestMatchers("/api/review/**").permitAll()

                                .requestMatchers("/api/wishlist/**").hasAuthority(UserRole.USER.name())

                                .requestMatchers("/api/admin/**").hasAuthority(UserRole.ADMIN.name())

                                .requestMatchers("/api/test/**").hasAuthority(UserRole.ADMIN.name())

                //.requestMatchers("/pages/**").permitAll()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .requestCache(cache -> cache.disable())
                .exceptionHandling(handling -> handling
                .defaultAuthenticationEntryPointFor(ajaxAuthenticationEntryPoint(), requestMatcher()))
                .formLogin(login -> login.disable())
                .httpBasic(Customizer.withDefaults());
                // .formLogin(login -> login
                //     .loginPage("/login").permitAll()
                // );

        return http.build();
    }
    
    @Bean
    protected WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
            new AntPathRequestMatcher("/static/**"),
            new AntPathRequestMatcher("/scripts/**"),
            new AntPathRequestMatcher("/images/**"),
            new AntPathRequestMatcher("/resources/**"),
            new AntPathRequestMatcher("/favicon.ico"),
            new AntPathRequestMatcher("/pages/**"),
            new AntPathRequestMatcher("/error"),
            new AntPathRequestMatcher("/swagger-ui/**"),
            new AntPathRequestMatcher("/v3/**")
        );
    }

    @Bean
    @SuppressWarnings("removal")
	protected AuthenticationManager authenticationManager(
        HttpSecurity http,
        UserDetailsService userDetailsService, 
        PasswordEncoder encoder
    ) throws Exception {
		return http.getSharedObject(AuthenticationManagerBuilder.class)
                   .userDetailsService(userDetailsService)
                   .passwordEncoder(encoder)
                   .and()
                   .build();
	}

    @SuppressWarnings("unused")
    @Bean
    protected AuthenticationEntryPoint ajaxAuthenticationEntryPoint() {
        return (HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) -> {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Некоректні введені дані користувача\"}");
        };
    }

    private RequestHeaderRequestMatcher requestMatcher() {
        return new RequestHeaderRequestMatcher("X-Requested-With", "XMLHttpRequest");
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Cache-Control"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}