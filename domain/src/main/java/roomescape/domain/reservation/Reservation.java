package roomescape.domain.reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/**
 * A pure domain object: no JPA annotation, no HTTP, no Spring.
 * Exactly the shape the roomescape project already has, only smaller.
 */
public class Reservation {

    private final Long id;
    private final String name;
    private final LocalDate date;
    private final LocalTime time;

    public Reservation(Long id, String name, LocalDate date, LocalTime time) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (date == null || time == null) {
            throw new IllegalArgumentException("date and time must not be null");
        }
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
    }

    public boolean isBefore(LocalDate other) {
        return date.isBefore(other);
    }

    public Reservation withId(Long newId) {
        return new Reservation(newId, name, date, time);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reservation other)) {
            return false;
        }
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
