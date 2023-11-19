package owlvernyte.springfood.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import owlvernyte.springfood.entity.*;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface BookingDetailRepository extends JpaRepository<BookingDetail, Long> {
    List<BookingDetail> findByBooking(Booking booking);

    BookingDetail findByBookingAndProduct(Booking booking, Product product);
    List<BookingDetail> findByDateTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);


    @Query("SELECT bd FROM BookingDetail bd WHERE YEAR(bd.dateTime) = :year AND MONTH(bd.dateTime) = :month")
    List<BookingDetail> findByDateTimeYearAndDateTimeMonth(@Param("year") int year, @Param("month") int month);
    @Query("SELECT SUM(bd.total) FROM BookingDetail bd WHERE YEAR(bd.dateTime) = :year")
    Double calculateRevenueByYear(@Param("year") int year);

}