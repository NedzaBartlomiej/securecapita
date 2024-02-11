package pl.bartlomiej.securecapita.common.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pl.bartlomiej.securecapita.common.security.auth.jwt.JwtAuthorizationFilter;
import pl.bartlomiej.securecapita.common.security.handler.AccesDeniedHandlerImpl;
import pl.bartlomiej.securecapita.common.security.handler.AuthenticationEntryPointImpl;

import java.util.Arrays;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final static String ROOT_V1_API_USERS_PATH = "/securecapita-api/v1/users";
    private static final String[] PUBLIC_USERS_ENDPOINTS = {
            "/auth/**",
            "/*/auth/**",
            "/verifications/**",
            "/*/verifications/**"
    };

    private final BCryptPasswordEncoder passwordEncoder;
    private final AccesDeniedHandlerImpl accesDeniedHandler;
    private final AuthenticationEntryPointImpl authenticationEntryPoint;
    private final UserDetailsService userDetailsService;
    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    private static String[] getPublicUsersEndpointsWithRootPath() {
        return Arrays.stream(PUBLIC_USERS_ENDPOINTS)
                .map(endpoint -> ROOT_V1_API_USERS_PATH + endpoint)
                .toArray(String[]::new);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagementConfigurer ->
                        sessionManagementConfigurer.sessionCreationPolicy(STATELESS))
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                        authorizationManagerRequestMatcherRegistry
                                // PUBLIC
                                .requestMatchers(POST, ROOT_V1_API_USERS_PATH).permitAll()
                                .requestMatchers(
                                        getPublicUsersEndpointsWithRootPath()).permitAll()
                                .requestMatchers("/error").permitAll()
                                // READ
                                .requestMatchers(GET, "/securecapita-api/v1/users/**").hasAuthority("READ:USER")
                                .requestMatchers(GET, "/securecapita-api/v1/customers/**").hasAuthority("READ:CUSTOMER")
                                // CREATE
                                .requestMatchers(POST, "/securecapita-api/v1/users/**").hasAuthority("CREATE:USER")
                                .requestMatchers(POST, "/securecapita-api/v1/customers/**").hasAuthority("CREATE:CUSTOMER")
                                // UPDATE
                                .requestMatchers(PUT, "/securecapita-api/v1/users/**").hasAuthority("UPDATE:USER")
                                .requestMatchers(PUT, "/securecapita-api/v1/customers/**").hasAuthority("UPDATE:CUSTOMER")
                                .requestMatchers(PATCH, "/securecapita-api/v1/users/**").hasAuthority("UPDATE:USER").requestMatchers(PUT, "/securecapita-api/v1/users/**").hasAuthority("UPDATE:USER")
                                .requestMatchers(PATCH, "/securecapita-api/v1/customers/**").hasAuthority("UPDATE:CUSTOMER")
                                // DELETE
                                .requestMatchers(DELETE, "/securecapita-api/v1/users/**").hasAuthority("DELETE:USER")
                                .requestMatchers(DELETE, "/securecapita-api/v1/customers/**").hasAuthority("DELETE:CUSTOMER")
                                .anyRequest().authenticated())
                .exceptionHandling(exceptionHandlingConfigurer ->
                        exceptionHandlingConfigurer
                                .accessDeniedHandler(accesDeniedHandler)
                                .authenticationEntryPoint(authenticationEntryPoint))
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .build();
    }

    @Bean
    AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authenticationProvider);
    }
}
