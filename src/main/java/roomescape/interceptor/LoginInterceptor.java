package roomescape.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.auth.JwtProvider;
import roomescape.domain.User;
import roomescape.exception.UnauthorizedException;

@Component
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {

    private final JwtProvider jwtProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        User user = extractUser(request);
        if (user == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }
        request.setAttribute("loginUser", user);
        return true;
    }

    private User extractUser(HttpServletRequest request) {
        // 웹: 세션에서 꺼내기
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("loginUser") != null) {
            return (User) session.getAttribute("loginUser");
        }

        // 모바일: Authorization 헤더에서 꺼내기
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtProvider.extract(token);
        }

        return null;
    }
}
