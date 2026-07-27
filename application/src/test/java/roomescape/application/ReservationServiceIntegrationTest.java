package roomescape.application;

import static org.assertj.core.api.Assertions.assertThat;
import static roomescape.domain.reservation.ReservationFixtures.aReservation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import roomescape.db.DatabaseCleaner;
import roomescape.domain.reservation.Reservation;

/**
 * This one test proves all three points of the article at once.
 *   1. ReservationFixtures comes from domain/src/testFixtures
 *   2. DatabaseCleaner comes from db/src/testFixtures
 *   3. the H2 driver is propagated through testFixtures(project(':db'))
 *      even though application never declares H2 itself
 *
 * Remove 'testImplementation testFixtures(project(":db"))' from
 * application/build.gradle and this test cannot even compile.
 * Change db's H2 back to testRuntimeOnly and it compiles but fails at runtime
 * with 'Failed to determine a suitable driver class'.
 */
@SpringBootTest
class ReservationServiceIntegrationTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void clearDatabase() {
        new DatabaseCleaner(jdbcTemplate).clear();
    }

    @Test
    void createThenFindAll() {
        Reservation given = aReservation().name("brown").build();

        reservationService.create(given.getName(), given.getDate(), given.getTime());

        assertThat(reservationService.findAll())
                .extracting(Reservation::getName)
                .containsExactly("brown");
    }
}
