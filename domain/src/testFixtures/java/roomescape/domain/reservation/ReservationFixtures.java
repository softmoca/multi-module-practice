package roomescape.domain.reservation;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Test-only builder with sane defaults. The equivalent of support/Fixtures
 * and support/ReservationTestHelper in the roomescape project.
 *
 * Why src/testFixtures and not src/test?
 *   src/test is private to this module - it is never packaged into a jar,
 *   so db and application tests could not import it.
 *   src/testFixtures produces a second jar (domain-...-test-fixtures.jar)
 *   that other modules can depend on explicitly.
 *
 * Why not put the defaults on the production constructor?
 *   Then production code could build a half-initialised Reservation.
 *   Keeping the defaults in a test-only class removes that risk.
 */
public class ReservationFixtures {

    private Long id = null;
    private String name = "brown";
    private LocalDate date = LocalDate.of(2026, 1, 1);
    private LocalTime time = LocalTime.of(10, 0);

    private ReservationFixtures() {
    }

    public static ReservationFixtures aReservation() {
        return new ReservationFixtures();
    }

    public ReservationFixtures id(Long id) {
        this.id = id;
        return this;
    }

    public ReservationFixtures name(String name) {
        this.name = name;
        return this;
    }

    public ReservationFixtures date(LocalDate date) {
        this.date = date;
        return this;
    }

    public ReservationFixtures time(LocalTime time) {
        this.time = time;
        return this;
    }

    public Reservation build() {
        return new Reservation(id, name, date, time);
    }
}
