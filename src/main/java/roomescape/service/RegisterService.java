package roomescape.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.exception.DuplicateLoginIdException;
import roomescape.repository.UserDao;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final UserDao userDao;

    public void register(String name, String loginId, String password) {
        userDao.findByLoginId(loginId).ifPresent(u -> {
            throw new DuplicateLoginIdException("이미 사용 중인 아이디입니다.");
        });
        userDao.save(name, loginId, password);
    }
}
