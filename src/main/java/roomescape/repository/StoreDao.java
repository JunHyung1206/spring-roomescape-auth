package roomescape.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import roomescape.domain.Store;
import roomescape.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

@Repository
public class StoreDao {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Store> rowMapper = (rs, rowNum) -> Store.create(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getLong("manager_id")
    );

    public StoreDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Store findById(long storeId) {
        String sql = "SELECT * FROM store WHERE id = ?";
        return jdbcTemplate.query(sql, rowMapper, storeId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("요청한 매장을 찾을 수 없습니다."));
    }

    public Optional<Store> findByManagerId(long managerId) {
        String sql = "SELECT * FROM store WHERE manager_id = ?";
        return jdbcTemplate.query(sql, rowMapper, managerId)
                .stream()
                .findFirst();
    }

    public List<Store> findAll() {
        return jdbcTemplate.query("SELECT * FROM store", rowMapper);
    }
}
