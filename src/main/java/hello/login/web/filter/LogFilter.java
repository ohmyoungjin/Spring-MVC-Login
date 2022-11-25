package hello.login.web.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

@Slf4j
public class LogFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("log filter init");
    }

    @Override
    public void destroy() {
        log.info("log filter destroy");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("log filter doFilter");

        //down casting
        //http 요청이 아닌 경우에도 사용할 수 있도록 하기 위해 ServletRequest 로 정의되어 있다.
        //http요청에 쓰기 위해선 다운 캐스팅이 필요하다.
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        String uuid = UUID.randomUUID().toString();

        try {
            log.info("REQUEST [{}][{}]", uuid, requestURI);
            //체인 쓰는 부분
            //다음 필터 호출하는 부분
            //다음 필터가 없는 경우 서블릿을 호출한다.
            //***이 부분이 없으면 다음 단계로 진행이 안된다 꼭 기재해줘야한다***
            chain.doFilter(request, response);
        } catch (Exception e) {
            throw  e;
        } finally {
            log.info("RESPONSE[{}][{}]", uuid, requestURI);
        }

    }
}
