package roomescape.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.domain.User;
import roomescape.exception.UnauthorizedException;
import roomescape.repository.UserDao;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final UserDao userDao;

    public User login(String loginId, String password) {
        return userDao.findByLoginId(loginId)
                .filter(m -> m.password().equals(password))
                .orElseThrow(() -> new UnauthorizedException("아이디 또는 비밀번호가 올바르지 않습니다."));
    }
}
