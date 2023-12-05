package owlvernyte.springfood.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import owlvernyte.springfood.entity.Booking;
import owlvernyte.springfood.entity.Desk;
import owlvernyte.springfood.entity.Order;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    boolean existsByDateArriveAndDesk(LocalDate dateArrive, String desk );

    @Query("SELECT o FROM Booking o WHERE CONCAT(o.name, o.code, o.phone, o.email) LIKE %?1%")
    List<Booking> findAll(String keyword);

    Page<Booking> findBookingByUserId(long userId, Pageable pageable);

    List<Booking> findByUserId(long userId);
    @Query("SELECT COUNT(t) FROM Booking t WHERE t.status = '1'")
    Long countHandlingBooking();
    @Query("SELECT COUNT(t) FROM Booking t WHERE t.status = '2'")
    Long countConfirmedBooking();
    @Query("SELECT COUNT(t) FROM Booking t WHERE t.status = '3'")
    Long countCancelBooking();
    @Query("SELECT COUNT(t) FROM Booking t WHERE t.status = '4'")
    Long countSuccessBooking();
    @Query("SELECT b FROM Booking b WHERE b.dateArrive = :date")
    List<Booking> findBookingsByDate(LocalDate date);

    Booking findByCode(String code);
    List<Booking> findByBookingDateAndReservationId(LocalDate dateArrive, long reservationId);
    List<Booking> findByDateArrive(LocalDate dateArrive);

    List<Booking> findByReservationId(long reservationId);
    Page<Booking> findByReservationId(long reservationId, Pageable pageable);


    long countByStatusInAndDateArrive(List<String> statuses, LocalDate dateArrive);

}
