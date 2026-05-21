package roomescape.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ThemeRequest(
        @NotBlank(message = "테마 이름은 필수입니다.")
        String name,
        String thumbnailUrl,
        String description,
        @NotNull(message = "매장 ID는 필수입니다.")
        Long storeId
) {
}
