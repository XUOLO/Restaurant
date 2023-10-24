package owlvernyte.springfood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import owlvernyte.springfood.entity.Booking;
import owlvernyte.springfood.entity.BookingDetail;
import owlvernyte.springfood.entity.Order;
import owlvernyte.springfood.entity.OrderDetail;
import owlvernyte.springfood.repository.BookingDetailRepository;

import java.util.List;

@Service
public class BookingDetailService {
@Autowired
    BookingDetailRepository bookingDetailRepository;

    public List<BookingDetail> getBookingDetailsByBooking(Booking booking) {
        return bookingDetailRepository.findByBooking(booking);
    }
}
