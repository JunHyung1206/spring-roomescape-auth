package roomescape.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String userPage() {
        return "forward:/user/index.html";
    }

    @GetMapping("/admin")
    public String adminPage() {
        return "forward:/admin/index.html";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "forward:/login/index.html";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "forward:/register/index.html";
    }

    @GetMapping("/manager")
    public String managerPage() {
        return "forward:/manager/index.html";
    }

    @GetMapping("/mobile")
    public String mobilePage() {
        return "forward:/mobile/index.html";
    }

    @GetMapping("/mobile/login")
    public String mobileLoginPage() {
        return "forward:/mobile/login/index.html";
    }
}
