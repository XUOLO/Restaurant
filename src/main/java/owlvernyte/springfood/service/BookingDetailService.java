package owlvernyte.springfood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import owlvernyte.springfood.entity.*;
import owlvernyte.springfood.repository.BookingDetailRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
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


    public double calculateRevenueByDate(LocalDate date) {
        LocalDateTime startDateTime = date.atStartOfDay();
        LocalDateTime endDateTime = date.atTime(23, 59, 59); // Kết thúc là 23:59:59 của ngày đó
        List<BookingDetail> bookingDetails = bookingDetailRepository.findByDateTimeBetween(startDateTime, endDateTime);

        double totalRevenue = 0.0;
        for (BookingDetail bookingDetail : bookingDetails) {
            totalRevenue += bookingDetail.getTotal();
        }
        return totalRevenue;
    }




    public double calculateRevenueByMonth(int year, int month) {
        List<BookingDetail> bookingDetails = bookingDetailRepository.findByDateTimeYearAndDateTimeMonth(year, month);

        double totalRevenue = 0.0;
        for (BookingDetail bookingDetail : bookingDetails) {
            totalRevenue += bookingDetail.getTotal();
        }
        return totalRevenue;
    }
    public double calculateRevenueByYear(int year) {
        Double totalRevenue = bookingDetailRepository.calculateRevenueByYear(year);
        return totalRevenue != null ? totalRevenue : 0.0;
    }
}
