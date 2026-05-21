package roomescape.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.auth.LoginContext;
import roomescape.domain.Reservation;
import roomescape.domain.Store;
import roomescape.domain.User;
import roomescape.repository.ReservationDao;
import roomescape.repository.StoreDao;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationQueryService {

    private final ReservationDao reservationDao;
    private final StoreDao storeDao;

    public List<Reservation> getAllReservations(User loginUser) {
        LoginContext ctx = loginContext(loginUser);
        if (ctx.isAdmin()) {
            return reservationDao.findAllReservations();
        }
        if (ctx.isStoreManager()) {
            return reservationDao.findByStoreId(ctx.managedStoreId());
        }
        return reservationDao.findByUserId(loginUser.id());
    }

    public List<Reservation> getByUserId(long userId) {
        return reservationDao.findByUserId(userId);
    }

    private LoginContext loginContext(User user) {
        Store managedStore = storeDao.findByManagerId(user.id()).orElse(null);
        return LoginContext.of(user, managedStore);
    }
}
