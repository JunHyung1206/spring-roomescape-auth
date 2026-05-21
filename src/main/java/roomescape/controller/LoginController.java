package roomescape.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.JwtProvider;
import roomescape.auth.LoginUser;
import roomescape.domain.Store;
import roomescape.domain.User;
import roomescape.dto.request.LoginRequest;
import roomescape.dto.response.LoginCheckResponse;
import roomescape.dto.response.MobileLoginResponse;
import roomescape.repository.StoreDao;
import roomescape.service.LoginService;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final JwtProvider jwtProvider;
    private final StoreDao storeDao;

    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        User loginUser = loginService.login(request.loginId(), request.password());
        session.setAttribute("loginUser", loginUser);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/login-check")
    public ResponseEntity<LoginCheckResponse> loginCheck(@LoginUser User user) {
        Store managedStore = storeDao.findByManagerId(user.id()).orElse(null);
        return ResponseEntity.ok(LoginCheckResponse.from(user, managedStore));
    }

    @PostMapping("/mobile/login")
    public ResponseEntity<MobileLoginResponse> mobileLogin(@Valid @RequestBody LoginRequest request) {
        User loginUser = loginService.login(request.loginId(), request.password());
        String token = jwtProvider.generate(loginUser);
        return ResponseEntity.ok(new MobileLoginResponse(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }
}
