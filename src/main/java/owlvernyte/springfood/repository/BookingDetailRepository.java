package owlvernyte.springfood.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import owlvernyte.springfood.entity.Booking;
import owlvernyte.springfood.entity.BookingDetail;
import owlvernyte.springfood.entity.Order;
import owlvernyte.springfood.entity.OrderDetail;

import java.util.List;


@Repository
public interface BookingDetailRepository extends JpaRepository<BookingDetail, Long> {
    List<BookingDetail> findByBooking(Booking booking);
}