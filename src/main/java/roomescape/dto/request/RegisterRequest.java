package roomescape.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank String name,
        @NotBlank String loginId,
        @NotBlank String password
) {}
