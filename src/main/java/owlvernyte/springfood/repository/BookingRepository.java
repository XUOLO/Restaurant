package owlvernyte.springfood.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import owlvernyte.springfood.entity.Booking;

import java.time.LocalDateTime;


@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    boolean existsByDateTime(LocalDateTime dateTime);

}
