package roomescape.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;
import roomescape.domain.User;
import roomescape.exception.ResourceNotFoundException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public class ReservationDao {

    private static final String SELECT_RESERVATION_BASE = """
            SELECT
                reservation.id          AS reservation_id,
                reservation.date        AS reservation_date,
                users.id                AS users_id,
                users.name              AS users_name,
                users.login_id          AS users_login_id,
                users.password          AS users_password,
                users.role              AS users_role,
                time.id                 AS time_id,
                time.start_at           AS time_value,
                theme.id                AS theme_id,
                theme.name              AS theme_name,
                theme.thumbnail_url     AS thumbnail_url,
                theme.description       AS theme_description,
                theme.store_id          AS theme_store_id
            FROM reservation
            INNER JOIN users            ON reservation.users_id = users.id
            INNER JOIN reservation_time AS time ON reservation.time_id = time.id
            INNER JOIN theme            ON reservation.theme_id = theme.id
            """;

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertExecutor;
    private final RowMapper<Reservation> rowMapper = (rs, rowNum) -> {

        User user = User.create(
                rs.getLong("users_id"),
                rs.getString("users_name"),
                rs.getString("users_login_id"),
                rs.getString("users_password"),
                rs.getString("users_role")
        );

        Theme theme = Theme.create(
                rs.getLong("theme_id"),
                rs.getString("theme_name"),
                rs.getString("thumbnail_url"),
                rs.getString("theme_description"),
                rs.getLong("theme_store_id")
        );

        ReservationTime reservationTime = ReservationTime.create(
                rs.getLong("time_id"),
                rs.getObject("time_value", LocalTime.class)
        );

        return Reservation.create(
                rs.getLong("reservation_id"),
                user,
                rs.getObject("reservation_date", LocalDate.class),
                reservationTime,
                theme
        );
    };

    public ReservationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertExecutor = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reservation")
                .usingGeneratedKeyColumns("id");
    }

    public Reservation save(long userId, LocalDate date, long timeId, long themeId) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("users_id", userId)
                .addValue("date", date)
                .addValue("time_id", timeId)
                .addValue("theme_id", themeId);

        Number reservationId = insertExecutor.executeAndReturnKey(params);
        return findById(reservationId.longValue());
    }

    public void delete(long reservationId) {
        String sql = "DELETE FROM reservation WHERE id = ?";
        int affected = jdbcTemplate.update(sql, reservationId);

        if (affected == 0) {
            throw new ResourceNotFoundException("요청한 예약을 찾을 수 없습니다.");
        }
    }

    public List<Reservation> findAllReservations() {
        return jdbcTemplate.query(SELECT_RESERVATION_BASE, rowMapper);
    }

    public Reservation findById(long reservationId) {
        String sql = SELECT_RESERVATION_BASE + " WHERE reservation.id = ?";
        return jdbcTemplate.query(sql, rowMapper, reservationId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("요청한 예약을 찾을 수 없습니다."));
    }

    public List<Reservation> findByUserId(long userId) {
        String sql = SELECT_RESERVATION_BASE + " WHERE users_id = ?";
        return jdbcTemplate.query(sql, rowMapper, userId);
    }

    public List<Reservation> findByStoreId(long storeId) {
        String sql = SELECT_RESERVATION_BASE + " WHERE theme.store_id = ?";
        return jdbcTemplate.query(sql, rowMapper, storeId);
    }

    public Reservation updateDateAndTime(long reservationId, LocalDate date, long timeId) {
        String sql = "UPDATE reservation SET date = ?, time_id = ? WHERE id = ?";
        jdbcTemplate.update(sql, date, timeId, reservationId);
        return findById(reservationId);
    }

    public boolean existsByDateAndTimeIdAndThemeId(LocalDate date, Long timeId, Long themeId) {
        String sql = """
                SELECT EXISTS (
                    SELECT 1 FROM reservation
                    WHERE date = ? AND time_id = ? AND theme_id = ?
                )
                """;
        return Boolean.TRUE.equals(
                jdbcTemplate.queryForObject(sql, Boolean.class, date, timeId, themeId)
        );
    }

    public boolean existsByDateAndTimeIdAndThemeIdExcluding(LocalDate date, long timeId, long themeId, long excludeId) {
        String sql = """
                SELECT EXISTS (
                    SELECT 1 FROM reservation
                    WHERE date = ? AND time_id = ? AND theme_id = ? AND id != ?
                )
                """;
        return Boolean.TRUE.equals(
                jdbcTemplate.queryForObject(sql, Boolean.class, date, timeId, themeId, excludeId)
        );
    }
}
