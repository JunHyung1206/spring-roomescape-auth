package roomescape.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.request.RegisterRequest;
import roomescape.service.RegisterService;

@RestController
@RequiredArgsConstructor
public class RegisterController {

    private final RegisterService registerService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        registerService.register(request.name(), request.loginId(), request.password());
        return ResponseEntity.ok().build();
    }
}
