package hello.login.web.interecptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Slf4j
public class LogInterceptor implements HandlerInterceptor {

    public static final String LOG_ID = "logId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI();
        String uuid = UUID.randomUUID().toString();

        //예외가 발생했을 때 afterCompletion, postHandle 에 넘기기 위해 저장해둔다.
        //호출 시점이 완전히 분리되어 함께 사용하기 어렵기 때문에 저장해둔다.
        request.setAttribute(LOG_ID, uuid);

        //@RequestMapping : HandlerMethod
        //정적 리소스 : ResourceHttpRequestHandler
        //정적 리소스가 넘어오는지 method가 넘어오는지 확인.
        if (handler instanceof HandlerMethod) {
            //handler = controller 정보를 가지고 있다.
            log.info("handler info {}" , handler);
            //호출할 컨트롤러 메서드의 모든 정보가 포함되어 있다.
            HandlerMethod hm = (HandlerMethod) handler;

        }

        log.info("Request preHandle [{}][{}][{}]", uuid, requestURI, handler);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("postHandle [{}]", modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String requestURI = request.getRequestURI();
        String logId = (String)request.getAttribute(LOG_ID);
        log.info("afterCompletion [{}][{}][{}]", logId, requestURI, handler);
        if (ex != null) {
            System.out.println("afterCompletion error " + ex);
        }
    }
}
