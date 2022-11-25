package hello.login;

import hello.login.web.filter.LogFilter;
import hello.login.web.filter.LoginCheckFilter;
import hello.login.web.interecptor.LogInterceptor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    //interceptor 사용하기 위해서는 WebMvcConfigurer 상속 받은 후
    //addInterceptors override 해줘야 한다.
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor())
                .order(1)
                .addPathPatterns("/**")
                //인터셉터를 사용하지 않을 부분 설정
                .excludePathPatterns("/css/**", "/*.ico", "/error");
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

    @Bean
    public FilterRegistrationBean logCheckFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        //filter class 설정
        filterRegistrationBean.setFilter(new LoginCheckFilter());
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
