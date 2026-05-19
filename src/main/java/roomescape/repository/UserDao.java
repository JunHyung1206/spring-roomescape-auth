package roomescape.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import roomescape.domain.User;
import roomescape.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

@Repository
public class UserDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertExecutor;
    private final RowMapper<User> rowMapper = (rs, rowNum) -> User.create(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("login_id"),
            rs.getString("password"),
            rs.getString("role")
    );

    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertExecutor = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
    }

    public User save(String name, String loginId, String password) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", name)
                .addValue("login_id", loginId)
                .addValue("password", password);

        Number userId = insertExecutor.executeAndReturnKey(params);
        return findById(userId.longValue());
    }

    public void delete(long userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        int affected = jdbcTemplate.update(sql, userId);

        if (affected == 0) {
            throw new ResourceNotFoundException("요청한 사용자를 찾을 수 없습니다.");
        }
    }

    public User findById(long userId) {
        String sql = "SELECT * FROM users WHERE id = ?";
        return jdbcTemplate.query(sql, rowMapper, userId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("요청한 사용자를 찾을 수 없습니다."));
    }

    public Optional<User> findByLoginId(String loginId) {
        String sql = "SELECT * FROM users WHERE login_id = ?";
        return jdbcTemplate.query(sql, rowMapper, loginId)
                .stream()
                .findFirst();
    }

    public List<User> findAll() {
        return jdbcTemplate.query("SELECT * FROM users", rowMapper);
    }
}
