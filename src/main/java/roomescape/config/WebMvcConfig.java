package roomescape.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.auth.LoginUserArgumentResolver;
import roomescape.interceptor.AdminInterceptor;
import roomescape.interceptor.AdminOnlyInterceptor;
import roomescape.interceptor.LoginInterceptor;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;
    private final AdminInterceptor adminInterceptor;
    private final AdminOnlyInterceptor adminOnlyInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/reservations/**", "/admin/**", "/login-check");

        // /admin/reservations/**: ADMIN 또는 매장 매니저
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/admin/reservations/**");

        // /admin/** 중 reservations 외(themes, times, users): ADMIN만
        registry.addInterceptor(adminOnlyInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/reservations/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginUserArgumentResolver());
    }
}
