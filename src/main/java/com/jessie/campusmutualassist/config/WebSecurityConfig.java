package com.jessie.campusmutualassist.config;


import com.jessie.campusmutualassist.filter.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.Filter;


@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter
{
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @Autowired
    private UserDetailServiceImpl userDetailServiceimpl;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    @Autowired
    private UserDetailServiceImpl theUserDetailService;
    @Autowired
    private MyAccessDeniedHandler myAccessDeniedHandler;


    @Value("${jwt.header}")//意思是从yml中去读
    private String tokenHeader;

    @Value("${jwt.route.authentication.path}")
    private String authenticationPath;

    //    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        // configure AuthenticationManager so that it knows from where to load
//        // user for matching credentials
//        // Use BCryptPasswordEncoder
//        auth.userDetailsService(userDetailServiceImpl).passwordEncoder(passwordEncoder());
//    }
    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception
    {
        return super.authenticationManagerBean();
    }


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception
    {
        auth
                // 从数据库读取的用户进行身份认证
                .userDetailsService(theUserDetailService).passwordEncoder(bCryptPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http
                .csrf().disable()
                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(myAccessDeniedHandler)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                // dont authenticate this particular request
                .and()
                .authorizeRequests()
                .antMatchers("/admin/**").hasAnyAuthority("admin")
                .antMatchers(authenticationPath).permitAll()
                .antMatchers("/user/Register").permitAll()
                .antMatchers("/user/login").permitAll()
                .antMatchers("/user/sendMail").permitAll()
                .antMatchers("/user/ResetPwByMail").permitAll()
                .antMatchers("/static/**").permitAll()
                .antMatchers("/compareCode").permitAll()
                .antMatchers("/user/**").permitAll()//方法权限比这个
                .antMatchers("/wechat/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore((Filter) jwtRequestFilter, (Class<? extends Filter>) UsernamePasswordAuthenticationFilter.class)
                //这里不知道为啥IDEA给我报错
                //逆大天了属于，还能强制转换成extends的？？？？？？？？？？？？？？？？？？？？
                //上一个项目也是这么写，没报错啊？？而且这个FilterExtend的类里确实implements了filter吧
                .formLogin()
                .loginProcessingUrl("/user/login")
                //.successForwardUrl("/auth")
                .failureForwardUrl("/user/loginError")
                .and()
                .logout()
                .logoutSuccessUrl("/user/logout")
        ;

    }

    @Override
    public void configure(WebSecurity web) throws Exception
    {
        // AuthenticationTokenFilter will ignore the below paths
        web
                .ignoring()
                .antMatchers(
                        authenticationPath
                )
                //不要对所有POST请求做限制
                // allow anonymous resource requests
                .and()
                .ignoring()
                .antMatchers(
                        HttpMethod.GET,
                        "/",
                        "/*.html",
                        "/favicon.ico",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js",
                        "/**/*.png",
                        "/*.png",
                        "/v2/api-docs",
                        "/swagger-resources/**",
                        "/swagger-ui.html",
                        "/v3/api-docs",
                        "/doc.html"


                );//mdzz忘记加swagger到忽略规则里了
    }

}
