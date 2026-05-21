package roomescape.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.auth.LoginContext;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;
import roomescape.domain.Store;
import roomescape.domain.User;
import roomescape.exception.DuplicateReservationException;
import roomescape.exception.ForbiddenException;
import roomescape.exception.InvalidReferenceException;
import roomescape.exception.PastReservationException;
import roomescape.exception.ResourceNotFoundException;
import roomescape.repository.ReservationDao;
import roomescape.repository.ReservationTimeDao;
import roomescape.repository.StoreDao;
import roomescape.repository.ThemeDao;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReservationCommandService {

    private final ReservationDao reservationDao;
    private final ReservationTimeDao reservationTimeDao;
    private final ThemeDao themeDao;
    private final StoreDao storeDao;

    private final Clock clock;

    private ReservationTime findTimeReference(long timeId) {
        try {
            return reservationTimeDao.findById(timeId);
        } catch (ResourceNotFoundException e) {
            throw new InvalidReferenceException("존재하지 않는 예약 시간입니다.");
        }
    }

    private void findThemeReference(long themeId) {
        try {
            themeDao.findById(themeId);
        } catch (ResourceNotFoundException e) {
            throw new InvalidReferenceException("존재하지 않는 테마입니다.");
        }
    }

    private boolean isPast(LocalDate date, ReservationTime time) {
        return date.atTime(time.startAt()).isBefore(LocalDateTime.now(clock));
    }

    private LoginContext loginContext(User user) {
        Store managedStore = storeDao.findByManagerId(user.id()).orElse(null);
        return LoginContext.of(user, managedStore);
    }

    private void checkPermission(User loginUser, Reservation reservation) {
        if (!loginContext(loginUser).canManage(reservation)) {
            throw new ForbiddenException("해당 예약에 접근할 권한이 없습니다.");
        }
    }

    public Reservation create(long userId, LocalDate date, long timeId, long themeId) {

        ReservationTime time = findTimeReference(timeId);
        findThemeReference(themeId);
        if (isPast(date, time)) {
            throw new PastReservationException("지나간 시간에는 예약을 생성할 수 없습니다.");
        }
        if (reservationDao.existsByDateAndTimeIdAndThemeId(date, timeId, themeId)) {
            throw new DuplicateReservationException("해당 날짜와 시간에 이미 예약이 존재합니다.");
        }
        return reservationDao.save(userId, date, timeId, themeId);
    }

    public void delete(long reservationId, User loginUser) {
        Reservation reservation = reservationDao.findById(reservationId);
        checkPermission(loginUser, reservation);
        reservationDao.delete(reservationId);
    }

    public void cancel(long reservationId, User loginUser) {
        Reservation reservation = reservationDao.findById(reservationId);
        checkPermission(loginUser, reservation);
        if (isPast(reservation.reservationDate(), reservation.reservationTime())) {
            throw new PastReservationException("이미 시작된 예약은 취소할 수 없습니다.");
        }
        reservationDao.delete(reservationId);
    }

    public Reservation update(long reservationId, LocalDate newDate, long newTimeId, User loginUser) {
        Reservation current = reservationDao.findById(reservationId);
        checkPermission(loginUser, current);

        ReservationTime newTime = findTimeReference(newTimeId);
        if (isPast(newDate, newTime)) {
            throw new PastReservationException("지나간 시간으로 예약을 변경할 수 없습니다.");
        }
        long themeId = current.reservationTheme().id();
        if (reservationDao.existsByDateAndTimeIdAndThemeIdExcluding(newDate, newTimeId, themeId, reservationId)) {
            throw new DuplicateReservationException("변경하려는 시간에 이미 다른 예약이 존재합니다.");
        }
        return reservationDao.updateDateAndTime(reservationId, newDate, newTimeId);
    }
}
