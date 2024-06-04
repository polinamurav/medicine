package com.example.medicine;////package com.Touristik1.Touristik1.config;

import com.example.medicine.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true) //для ролей
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                // Добавляем разрешение для URL-адреса /edit-acc-user/{id}
                .antMatchers("/edit-acc-user/{id}").authenticated()
                // Другие разрешения
                .antMatchers("/", "/services", "/contacts", "/images/**", "/doctor-images/**", "/registration", "/img/**", "/css/**")
                .permitAll()
                .antMatchers("/doctor/**").hasAuthority("ROLE_DOCTOR")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .successHandler(successHandler())
                .and()
                .logout()
                .logoutSuccessUrl("/")
                .permitAll();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); //защита на 12
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return new CustomAuthenticationSuccessHandler();
    }


}
