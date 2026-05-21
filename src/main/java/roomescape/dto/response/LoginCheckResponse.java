package roomescape.dto.response;

import roomescape.domain.Store;
import roomescape.domain.User;

public record LoginCheckResponse(
        String name,
        String role,
        Long managedStoreId,
        String managedStoreName
) {
    public static LoginCheckResponse from(User user, Store managedStore) {
        if (managedStore == null) {
            return new LoginCheckResponse(user.name(), user.role().name(), null, null);
        }
        return new LoginCheckResponse(user.name(), user.role().name(), managedStore.id(), managedStore.name());
    }
}
