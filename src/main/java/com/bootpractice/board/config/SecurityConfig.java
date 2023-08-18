package com.bootpractice.board.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic().disable() // http basic 방식은 사용하지 않고 jwt를 사용할 것임
                .csrf().disable() // 세션이 아니기 때문에 csrf 취약점 보호는 하지 않음
                .cors() // cors 설정
                .and()  // 다음 설정으로
                .authorizeRequests() // 요청 authorize 설정을 시작
                .antMatchers("/api/members/join","/api/members/login").permitAll() // 가입과 로그인은 authorize없이 허용
                .antMatchers("/api/**").authenticated() // 위 요청을 제외한 api 요청은 항상 인증이 필요
                .and() // 다음 설정으로
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) //jwt stateless 방식을 사용하기위해 설정
                .and() // 다음 설정으로
                //.addFilterBefore(new JwtTokenFilter(memberService, secretKey), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
