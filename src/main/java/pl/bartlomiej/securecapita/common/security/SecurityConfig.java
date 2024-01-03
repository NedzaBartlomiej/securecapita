package pl.bartlomiej.securecapita.common.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import pl.bartlomiej.securecapita.common.security.handler.AccesDeniedHandlerImpl;
import pl.bartlomiej.securecapita.common.security.handler.AuthenticationEntryPointImpl;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] PUBLIC_ENDPOINTS = {};
    private final BCryptPasswordEncoder passwordEncoder;
    private final AccesDeniedHandlerImpl accesDeniedHandler;
    private final AuthenticationEntryPointImpl authenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagementConfigurer ->
                        sessionManagementConfigurer.sessionCreationPolicy(STATELESS))
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                        authorizationManagerRequestMatcherRegistry
                                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                                .requestMatchers(DELETE, "/securecapita-api/v1/users/**").hasAuthority("DELETE:USER")
                                .requestMatchers(DELETE, "/securecapita-api/v1/customers/**").hasAuthority("DELETE:CUSTOMER")
                                .anyRequest().authenticated())
                .exceptionHandling(exceptionHandlingConfigurer ->
                        exceptionHandlingConfigurer
                                .accessDeniedHandler(accesDeniedHandler)
                                .authenticationEntryPoint(authenticationEntryPoint))
                .build();
    }

    // authenticationManager.authenticate(Authentication authentication)
    // -> return Authentication object (with this we can generate for example: JWT TOKEN)
    @Bean
    AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(); //todo
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authenticationProvider);
    }
}
