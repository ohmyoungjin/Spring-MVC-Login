package hello.login.web.filter;

import hello.login.web.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.http.HttpResponse;

@Slf4j
public class LoginCheckFilter implements Filter {

    //로그인 검증필터에서 제외할 url
    private static final String[] whitelist = {"/", "/members/add", "/login", "/logout", "/css/*"};

    //interface에 default 키워드가 붙어있으면 override 안해줘도 된다.

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        HttpServletResponse httpResponse = (HttpServletResponse) response;
        try {
            log.info("인증 체크 필터 시작 {}", requestURI);

            if (isLoginCheckPath(requestURI)) {
                log.info("인증 체크 로직 실행 {}", requestURI);
                HttpSession session = httpRequest.getSession(false);
                if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {

                    log.info("미인증 사용자 요청 {}", requestURI);
                    //로그인 페이지로 redirect
                    //이후 로그인 되면 처음 사용하려 했던 페이지 ex)상품 관리, 등록 페이지로 넘겨주기 위해
                    //?redirectURL 넣어준다.
                    //***URL적어줄 때 띄어쓰기 있으면 인코딩 돼서 이상한 문자가 들어가서 제대로 실행되지 않는다.
                    //redirectURL = 이렇게 적으면 안 됨.
                    httpResponse.sendRedirect("/login?redirectURL=" + requestURI);
                    //**중요한 부분
                    //return 으로 로직자체를 더 이상 진행하지 않는다.
                    //이후 처리 되야 할 filter, servlet , controller 진행하지 않는다.
                    return;
                }
            }

            chain.doFilter(request, response);
        } catch (Exception e) {
            //예외 로깅 가능 하지만, 톰캣까지 예외를 보내주어야 함
            throw e;
        } finally {
            log.info("인증 체크 필터 종료 {}", requestURI);
        }

    }

    /**
     * 화이트 리스트의 경우 인증 체크X
     */
    private boolean isLoginCheckPath(String requestURI) {
        //Spring 에서 제공 하는 pattern check 유틸
        return !PatternMatchUtils.simpleMatch(whitelist, requestURI);
    }
}
