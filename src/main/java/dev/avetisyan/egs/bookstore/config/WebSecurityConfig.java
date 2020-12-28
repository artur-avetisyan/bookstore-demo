package dev.avetisyan.egs.bookstore.config;

import dev.avetisyan.egs.bookstore.auth.UserDetailService;
import dev.avetisyan.egs.bookstore.auth.UserRole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(HttpMethod.POST, "/users");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.
                // In the production code I'll consider providing csrf token,
                // disabling csrf here for convenience
                csrf().disable().
                authorizeRequests().
                // also can be done using annotations @PreAuthorize, @Secured or @RoleAllowed with additional config
                antMatchers(HttpMethod.GET, "/users").hasAuthority(UserRole.ADMIN.getName()).
                antMatchers(HttpMethod.PUT, "/authors/*").hasAuthority(UserRole.ADMIN.getName()).
                antMatchers(HttpMethod.PATCH, "/authors/*/approval").hasAuthority(UserRole.ADMIN.getName()).
                antMatchers(HttpMethod.PATCH, "/books/*/approval").hasAuthority(UserRole.ADMIN.getName()).
                anyRequest().authenticated().
                and().
                formLogin().permitAll().
                and().
                logout().permitAll().
                and().
                exceptionHandling().
                accessDeniedHandler((request, response, accessDeniedException) ->
                        response.setStatus(HttpStatus.FORBIDDEN.value()));
    }
}
