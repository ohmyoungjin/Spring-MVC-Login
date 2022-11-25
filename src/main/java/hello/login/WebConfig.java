package hello.login;

import hello.login.web.argumentresolver.LoginMemberArgumentResolver;
import hello.login.web.filter.LogFilter;
import hello.login.web.filter.LoginCheckFilter;
import hello.login.web.interecptor.LogInterceptor;
import hello.login.web.interecptor.LoginCheckInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

//    private final LoginCheckFilter loginCheckFilter;
//
//    @Autowired
//    public WebConfig(LoginCheckFilter loginCheckFilter) {
//        this.loginCheckFilter = loginCheckFilter;
//    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginMemberArgumentResolver());
    }

    //interceptor 사용하기 위해서는 WebMvcConfigurer 상속 받은 후
    //addInterceptors override 해줘야 한다.
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor())
                //chain 개념 순서 설정
                .order(1)
                //검증할 url 설정 부분
                .addPathPatterns("/**")
                //검증에서 뺄 url 설정 부분
                .excludePathPatterns("/css/**", "/*.ico", "/error");

        registry.addInterceptor(new LoginCheckInterceptor())
                .order(2)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/", "/members/add", "/login", "/logout",
                        "/css/**", "/*.ico", "/error"
                );
    }

//    @Bean
    public FilterRegistrationBean logFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        //filter class 설정
        filterRegistrationBean.setFilter(new LogFilter());
        filterRegistrationBean.setOrder(1);
        //filter url 설정 /*는 모든 url에 적용함
        filterRegistrationBean.addUrlPatterns("/*");

        return filterRegistrationBean;
    }

//    @Bean
    public FilterRegistrationBean logCheckFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        //filter class 설정
        filterRegistrationBean.setFilter(new LoginCheckFilter());
        //의존성 주입으로 사용 가능하다.
        //filterRegistrationBean.setFilter(loginCheckFilter);
        //chain 순서 설정하는 부분 1 = > 2
        filterRegistrationBean.setOrder(2);
        //filter url 설정 /*는 모든 url에 적용함
        //여기서 따로따로 설정해주지 말고 해당하는 class에서 적용할 부분 뺄 부분 넣어주도록 함
        //모든 부분을 호출 할 때 이 로직을 타기 때문에
        //이렇게 설정하면 성능이 저하 되지 않나 싶지만 성능에 영항을 주는 부분은 미비하다.
        filterRegistrationBean.addUrlPatterns("/*");

        return filterRegistrationBean;
    }
}
