package hello.login.web;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MemberRepository memberRepository;
    private final SessionManager sessionManager;
    //@GetMapping("/")
    public String home() {
        return "home";
    }
    //보안 문제
    //쿠키 값은 임의로 변경할 수 있다.
    //클라이언트가 쿠키를 강제로 변경하면 다른 사용자가 된다.
    //실제 웹브라우저 개발자모드 Application Cookie 변경으로 확인
    //Cookie: memberId=1 Cookie: memberId=2 (다른 사용자의 이름이 보임)
    //쿠키에 보관된 정보는 훔쳐갈 수 있다.
    //만약 쿠키에 개인정보나, 신용카드 정보가 있다면?
    //이 정보가 웹 브라우저에도 보관되고, 네트워크 요청마다 계속 클라이언트에서 서버로 전달된다.
    //쿠키의 정보가 나의 로컬 PC에서 털릴 수도 있고, 네트워크 전송 구간에서 털릴 수도 있다.
    //해커가 쿠키를 한번 훔쳐가면 평생 사용할 수 있다.
    //해커가 쿠키를 훔쳐가서 그 쿠키로 악의적인 요청을 계속 시도할 수 있다.
    //대안
    //쿠키에 중요한 값을 노출하지 않고, 사용자 별로 예측 불가능한 임의의 토큰(랜덤 값)을 노출하고, 서버에서
    //토큰과 사용자 id를 매핑해서 인식한다. 그리고 서버에서 토큰을 관리한다.
    //토큰은 해커가 임의의 값을 넣어도 찾을 수 없도록 예상 불가능 해야 한다.
    //해커가 토큰을 털어가도 시간이 지나면 사용할 수 없도록 서버에서 해당 토큰의 만료시간을 짧게(예: 30분)
    //유지한다. 또는 해킹이 의심되는 경우 서버에서 해당 토큰을 강제로 제거하면 된다.
//    @GetMapping("/")
    public String homeLogin(
            @CookieValue(name = "memberId", required = false) Long memberId,
            Model model) {
        if (memberId == null) {
            return "home";
        }
        //로그인
        Member loginMember = memberRepository.findById(memberId);
        if (loginMember == null) {
            return "home";
        }
        model.addAttribute("member", loginMember);
        return "loginHome";
    }

//    @GetMapping("/")
    public String homeLoginV2(HttpServletRequest request, Model model){
        Member member = (Member)sessionManager.getSession(request);
        if (member == null) {
            return "home";
        }
        //로그인
        model.addAttribute("member", member);
        return "loginHome";
    }

//    @GetMapping("/")
    public String homeLoginV3(HttpServletRequest request, Model model){
        //로그인 하면 생성을 해야 한다.
        //웹 페이지에 접속 했다고 해서 세션을 만들면 안된다.
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "home";
        }

        Member loginMember = (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);

        if (loginMember == null) {
            return "home";
        }
        //로그인
        model.addAttribute("member", loginMember);
        return "loginHome";
    }

    @GetMapping("/")
    public String homeLoginV3Spring(
            @SessionAttribute(name= SessionConst.LOGIN_MEMBER, required = false) Member loginMember, Model model){
        //@SessionAttribute를 사용하면
        //HttpSession session = request.getSession(false);
        //Member loginMember = (Member)session.getAttribute(SessionConst.LOGIN_MEMBER);
        //세션을 따로 생성하지 않는다.
        if (loginMember == null) {
            return "home";
        }
        //로그인
        model.addAttribute("member", loginMember);
        return "loginHome";
    }

}