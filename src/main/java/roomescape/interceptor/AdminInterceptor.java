package roomescape.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.domain.User;
import roomescape.exception.UnauthorizedException;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        User loginUser = (User) request.getAttribute("loginUser");
        if (!loginUser.isAdmin()) {
            throw new UnauthorizedException("관리자 권한이 필요합니다.");
        }
        return true;
    }
}
