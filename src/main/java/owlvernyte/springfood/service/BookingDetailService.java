package owlvernyte.springfood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import owlvernyte.springfood.entity.*;
import owlvernyte.springfood.repository.BookingDetailRepository;

import java.util.List;

@Service
public class BookingDetailService {
@Autowired
    BookingDetailRepository bookingDetailRepository;

    public List<BookingDetail> getBookingDetailsByBooking(Booking booking) {
        return bookingDetailRepository.findByBooking(booking);
    }


    public BookingDetail findBookingDetailByBookingAndProduct(Booking booking, Product product) {
        return bookingDetailRepository.findByBookingAndProduct(booking, product);
    }
}
