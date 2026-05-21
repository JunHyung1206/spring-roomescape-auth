package roomescape.auth;

import lombok.RequiredArgsConstructor;
import roomescape.domain.Reservation;
import roomescape.domain.Store;
import roomescape.domain.User;

import java.util.Objects;

@RequiredArgsConstructor
public class LoginContext {

    private final User user;
    private final Store managedStore;

    public static LoginContext of(User user, Store managedStore) {
        return new LoginContext(user, managedStore);
    }

    public User user() {
        return user;
    }

    public boolean isAdmin() {
        return user.isAdmin();
    }

    public boolean isStoreManager() {
        return managedStore != null;
    }

    public boolean canManage(Reservation reservation) {
        if (user.isAdmin()) {
            return true;
        }
        if (isStoreManager() && Objects.equals(managedStore.id(), reservation.reservationTheme().storeId())) {
            return true;
        }
        return Objects.equals(user.id(), reservation.user().id());
    }

    public Long managedStoreId() {
        return managedStore == null ? null : managedStore.id();
    }
}
