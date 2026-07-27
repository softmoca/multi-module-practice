package roomescape.domain.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static roomescape.domain.reservation.ReservationFixtures.aReservation;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * A plain JUnit test. Note that src/test can use src/testFixtures of the same
 * module for free - the java-test-fixtures plugin wires that up.
 * Note also that THIS class is never visible to db or application.
 * Only what sits in testFixtures is published.
 */
class ReservationTest {

    @Test
    @DisplayName("a blank name is rejected")
    void blankName() {
        assertThatThrownBy(() -> aReservation().name(" ").build())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("isBefore compares the date only")
    void isBefore() {
        Reservation reservation = aReservation().date(LocalDate.of(2026, 1, 1)).build();

        assertThat(reservation.isBefore(LocalDate.of(2026, 1, 2))).isTrue();
        assertThat(reservation.isBefore(LocalDate.of(2025, 12, 31))).isFalse();
    }
}
