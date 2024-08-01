package gather.here.api.infra.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import gather.here.api.application.service.TokenService;
import gather.here.api.domain.repositories.MemberRepository;
import gather.here.api.domain.security.UserDetailServiceImpl;
import gather.here.api.infra.crypto.CustomPasswordEncoder;
import gather.here.api.infra.security.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig{

    private final ObjectMapper objectMapper;
    private final ObjectPostProcessor<Object> objectPostProcessor;
    private final MemberRepository memberRepository;
    private final TokenService tokenService;

    private static final String[] AUTH_WHITELIST = {
            "/members",
            "/login",
            "/v3/**", // v3 : SpringBoot 3(없으면 swagger 예시 api 목록 제공)
            "/swagger-ui/**"
    };

    @Bean
    @ConditionalOnProperty(name = "spring.h2.console.enabled", havingValue = "true")
    public WebSecurityCustomizer configureH2ConsoleEnable() {
        return web -> web.ignoring()
                .requestMatchers(PathRequest.toH2Console());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                    request -> request
                        .requestMatchers(AUTH_WHITELIST).permitAll()
                        .anyRequest().authenticated()
                );

        httpSecurity.addFilterAt(loginProcessingFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class);
        httpSecurity.addFilterAt(jwtAuthenticationProcessingFilter(), LoginProcessingFilter.class);
        httpSecurity.exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint()));

        return httpSecurity.build();
    }

    @Bean
    public AbstractAuthenticationProcessingFilter loginProcessingFilter(
            AuthenticationManager authenticationManager) {
        LoginProcessingFilter loginProcessingFilter = new LoginProcessingFilter(objectMapper);

        // AuthenticationManager 설정
        loginProcessingFilter.setAuthenticationManager(authenticationManager);

        // Handler 설정
        loginProcessingFilter.setAuthenticationSuccessHandler(new LoginSuccessHandler(tokenService));
        loginProcessingFilter.setAuthenticationFailureHandler(new LoginFailureHandler(objectMapper));

        return loginProcessingFilter;
    }
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        AuthenticationManagerBuilder builder = new AuthenticationManagerBuilder(objectPostProcessor);

        // UserDetailsService, PasswordEncoder 설정
        builder.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
        return builder.build();
    }

    @Bean
    public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() {
        return new JwtAuthenticationProcessingFilter(tokenService);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new CustomPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailServiceImpl(memberRepository);
    }

    @Bean
    AuthenticationSuccessHandler authenticationSuccessHandler(){
        return new LoginSuccessHandler(tokenService);
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint(objectMapper);
    }
}
