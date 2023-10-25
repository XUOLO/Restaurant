package owlvernyte.springfood.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import owlvernyte.springfood.entity.Booking;
import owlvernyte.springfood.entity.Order;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    boolean existsByDateTime(LocalDateTime dateTime);
    @Query("SELECT o FROM Booking o WHERE CONCAT(o.name, o.code, o.phone, o.email) LIKE %?1%")
    List<Booking> findAll(String keyword);



    @Query("SELECT COUNT(t) FROM Booking t WHERE t.status = '1'")
    Long countHandlingBooking();
    @Query("SELECT COUNT(t) FROM Booking t WHERE t.status = '2'")
    Long countConfirmedBooking();
    @Query("SELECT COUNT(t) FROM Booking t WHERE t.status = '3'")
    Long countCancelBooking();
}
