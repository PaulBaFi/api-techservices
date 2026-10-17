package pe.techservices.api_techservices.security;

import org.springframework.context.annotation.*;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pe.techservices.api_techservices.service.AuthService;

@Configuration
public class SecurityConfig {
    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
    @Bean ApiKeyTokenFilter apiKeyTokenFilter(AuthService authService) { return new ApiKeyTokenFilter(authService); }
    @Bean FilterRegistrationBean<ApiKeyTokenFilter> apiKeyTokenFilterRegistration(ApiKeyTokenFilter filter) {
        FilterRegistrationBean<ApiKeyTokenFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
    @Bean SecurityFilterChain securityFilterChain(HttpSecurity http, ApiKeyTokenFilter filter) throws Exception {
        return http.csrf(csrf -> csrf.disable()).formLogin(form -> form.disable()).httpBasic(basic -> basic.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(JsonSecurityHandlers.authenticationEntryPoint())
                        .accessDeniedHandler(JsonSecurityHandlers.accessDeniedHandler()))
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .addFilterAfter(filter, SecurityContextHolderFilter.class)
                .addFilterBefore(filter, AnonymousAuthenticationFilter.class).build();
    }
}
