package roomescape.domain.reservation;

import java.util.List;

/**
 * The port. The interface stays in the domain module, the JDBC implementation
 * lives in the db module. This dependency inversion is what makes the split
 * possible at all - domain never points outwards.
 */
public interface ReservationRepository {

    Reservation save(Reservation reservation);

    List<Reservation> findAll();
}
