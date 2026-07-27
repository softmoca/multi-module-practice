package roomescape.db;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * The roomescape project keeps this in support/DatabaseCleaner and every test
 * uses it. Once the modules are split, the acceptance tests in application
 * still need it - so it belongs in db/src/testFixtures, next to the JDBC code
 * it knows about, and is published to whoever asks for db's fixtures.
 */
public class DatabaseCleaner {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseCleaner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void clear() {
        jdbcTemplate.execute("set referential_integrity false");
        jdbcTemplate.execute("truncate table reservation");
        jdbcTemplate.execute("alter table reservation alter column id restart with 1");
        jdbcTemplate.execute("set referential_integrity true");
    }
}
