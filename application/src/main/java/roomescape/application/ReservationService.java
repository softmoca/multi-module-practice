package roomescape.application;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;

/**
 * The service depends on the port only. It has never heard of JdbcTemplate,
 * H2 or the db module - and the build makes sure it cannot.
 */
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public Reservation create(String name, LocalDate date, LocalTime time) {
        return reservationRepository.save(new Reservation(null, name, date, time));
    }

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }
}
