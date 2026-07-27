package roomescape.db;

import static org.assertj.core.api.Assertions.assertThat;
import static roomescape.domain.reservation.ReservationFixtures.aReservation;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import roomescape.domain.reservation.Reservation;

/**
 * Two imports to pay attention to:
 *   ReservationFixtures - comes from testFixtures(project(':domain'))
 *   the H2 driver        - comes from testFixturesRuntimeOnly in this module
 * Remove either declaration from db/build.gradle and this test breaks.
 */
class JdbcReservationRepositoryTest {

    private EmbeddedDatabase database;
    private JdbcReservationRepository repository;

    @BeforeEach
    void setUp() {
        database = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("schema.sql")
                .build();
        repository = new JdbcReservationRepository(new JdbcTemplate(database));
    }

    @AfterEach
    void tearDown() {
        database.shutdown();
    }

    @Test
    void saveAssignsAnIdAndFindAllReadsItBack() {
        Reservation saved = repository.save(aReservation().name("brown").build());

        assertThat(saved.getId()).isNotNull();
        assertThat(repository.findAll())
                .extracting(Reservation::getName)
                .containsExactly("brown");
    }
}
