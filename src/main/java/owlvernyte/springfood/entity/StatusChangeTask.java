package owlvernyte.springfood.entity;

import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import owlvernyte.springfood.service.BookingService;

import java.util.TimerTask;

public class StatusChangeTask extends TimerTask {
    private Booking booking;
    @Autowired
    private BookingService bookingService;



    public StatusChangeTask(Booking booking, BookingService bookingService) {
        this.booking = booking;
        this.bookingService = bookingService;
    }

    @Override
    public void run() {
        // Thực hiện thay đổi trạng thái của booking thành 1
        booking.setStatus("3");
        bookingService.saveBooking(booking);
    }
}