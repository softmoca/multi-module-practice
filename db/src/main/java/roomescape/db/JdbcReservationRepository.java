package roomescape.db;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Time;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;

/**
 * The adapter. It knows SQL; the domain does not.
 * application never sees this class at compile time (runtimeOnly project(':db')).
 */
@Repository
public class JdbcReservationRepository implements ReservationRepository {

    private static final RowMapper<Reservation> ROW_MAPPER = (rs, rowNum) -> new Reservation(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getDate("reservation_date").toLocalDate(),
            rs.getTime("reservation_time").toLocalTime()
    );

    private final JdbcTemplate jdbcTemplate;

    public JdbcReservationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Reservation save(Reservation reservation) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "insert into reservation (name, reservation_date, reservation_time) values (?, ?, ?)",
                    new String[]{"id"});
            ps.setString(1, reservation.getName());
            ps.setDate(2, Date.valueOf(reservation.getDate()));
            ps.setTime(3, Time.valueOf(reservation.getTime()));
            return ps;
        }, keyHolder);

        return reservation.withId(keyHolder.getKey().longValue());
    }

    @Override
    public List<Reservation> findAll() {
        return jdbcTemplate.query(
                "select id, name, reservation_date, reservation_time from reservation order by id",
                ROW_MAPPER);
    }
}
