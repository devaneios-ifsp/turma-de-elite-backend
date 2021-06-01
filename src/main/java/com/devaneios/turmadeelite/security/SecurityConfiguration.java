package com.devaneios.turmadeelite.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    FirebaseAuthenticationProvider firebaseAuthenticationProvider;

    private static final RequestMatcher PROTECTED_URLS = new OrRequestMatcher(
            new AntPathRequestMatcher("/auth/**")
    );

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .anonymous().disable()
                .cors()
                .and()
                .csrf().disable()
//                .authorizeRequests().requestMatchers(PROTECTED_URLS).authenticated()
//                .and()
                .authorizeRequests().requestMatchers(PROTECTED_URLS).permitAll()
                .and()
                .exceptionHandling().authenticationEntryPoint((request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Não autorizado"))
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManager(){
        return new ProviderManager(Arrays.asList(firebaseAuthenticationProvider));
    }

    public BearerTokenFilter authenticationFilter(){
        BearerTokenFilter bearerTokenFilter = new BearerTokenFilter();
        bearerTokenFilter.setAuthenticationManager(authenticationManager());
        bearerTokenFilter.setAuthenticationSuccessHandler((request, response, authentication) -> {});
        return bearerTokenFilter;
    }

    @Bean
    PasswordEncoder getPasswordEncoder(){
        return new Argon2PasswordEncoder();
    }

}
