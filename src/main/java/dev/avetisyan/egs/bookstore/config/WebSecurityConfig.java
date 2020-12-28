package dev.avetisyan.egs.bookstore.config;

import dev.avetisyan.egs.bookstore.auth.UserDetailService;
import dev.avetisyan.egs.bookstore.auth.UserRole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
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
    protected void configure(HttpSecurity http) throws Exception {
        http.
                // In production code I'll provide csrf token,
                // disabling csrf here for convenience
                csrf().disable().
                authorizeRequests().

                // FIXME: fix ant matchers
//                antMatchers("/").hasAnyAuthority(UserRole.ADMIN.getName(), UserRole.USER.getName()).
//                antMatchers("/new").hasAnyAuthority(UserRole.ADMIN.getName(), UserRole.USER.getName()).
//                antMatchers("/edit/**").hasAnyAuthority(UserRole.ADMIN.getName(), UserRole.USER.getName()).
//                antMatchers("/delete/**").hasAuthority(UserRole.ADMIN.getName()).
//                antMatchers("/users").permitAll().
                anyRequest().authenticated().
                and().
                formLogin().permitAll().
                and().
                logout().permitAll().
                and().
                exceptionHandling().accessDeniedPage("/403");
    }
}
