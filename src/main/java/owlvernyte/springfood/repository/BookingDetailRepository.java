package owlvernyte.springfood.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import owlvernyte.springfood.entity.Booking;
import owlvernyte.springfood.entity.BookingDetail;


@Repository
public interface BookingDetailRepository extends JpaRepository<BookingDetail, Long> {

}